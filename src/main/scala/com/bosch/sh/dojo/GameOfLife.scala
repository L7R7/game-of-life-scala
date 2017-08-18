/*
 * Copyright 2017 Marco Ehrentreich <marco.ehrentreich@bosch.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import scala.collection.mutable
import scala.util.Random

object GameOfLife extends App {
  trait CellStatus
  object Alive extends CellStatus {
    override def toString: String = "o"
  }
  object Dead extends CellStatus {
    override def toString: String = "x"
  }
  case class Pos(x: Int, y: Int) {
    lazy val neighbors: Iterable[Pos] = {
      val all = for {
        neighborY <- this.y - 1 to this.y + 1
        neighborX <- this.x - 1 to this.x + 1
      } yield Pos(neighborX, neighborY)
      all.filterNot(_ == this)
    }
  }

  case class Cell(status: CellStatus) {
    override def toString: String = status.toString
  }

  object Board {
    def apply(percentage: Int = 30, width: Int = 50, height: Int = 30): Board = {
      val numCells: Int = (width - 2) * (height - 2)
      val aliveCells: Int = (numCells * percentage / 100.0).floor.toInt
      val initialCellStatus: List[CellStatus] = Iterator.range(1, numCells + 1)
                                                .map(index => if (index <= aliveCells) Alive else Dead)
                                                .toList
      val shuffled: List[CellStatus] = Random.shuffle(initialCellStatus)

      val buffer = mutable.Map[Pos, Cell]()
      for {
        y <- 0 until height
        x <- 0 until width
      } if (x == 0 || x == width - 1 || y == 0 || y == height - 1) buffer(Pos(x, y)) = Cell(Dead)
      else buffer(Pos(x, y)) = Cell(shuffled((y - 1) * (width - 2) + (x - 1)))

      new Board(buffer.toMap, width, height)
    }
  }
  case class Board private(cells: Map[Pos, Cell], width: Int, height: Int) {
    override def toString: String = {
      cells.toSeq
      .sortWith((a: (Pos, Cell), b: (Pos, Cell)) => a._1.x.compareTo(b._1.x) < 0)
      .sortWith((a: (Pos, Cell), b: (Pos, Cell)) => a._1.y.compareTo(b._1.y) < 0)
      .map(_._2.toString)
      .mkString(" ")
      .grouped(2 * width)
      .foldLeft("")((acc: String, e: String) => acc + e + "\r\n")
    }

    def sumAlive(positions: Iterable[Pos], cells: Map[Pos, Cell]): Int =
      positions map (cells(_)) count (_.status == Alive)

    def next(): Board = {
      val newCells = cells.map { entry =>
        if (entry._1.x == 0 || entry._1.x == width - 1 || entry._1.y == 0 || entry._1.y == height - 1) entry
        else {
          val aliveNeighbors = sumAlive(entry._1.neighbors, cells)
          entry._1 -> (if (aliveNeighbors == 2 || aliveNeighbors == 3) Cell(Alive) else Cell(Dead))
        }
      }
      copy(cells = newCells)
    }
  }

  private val boardsEierer: Iterator[Board] = Iterator.iterate(Board(percentage = 31, width = 1000, height = 10000)) { board =>
    Thread.sleep(500)
    board.next()
  }
  boardsEierer take 20 foreach println
}