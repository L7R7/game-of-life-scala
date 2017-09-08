/*
 * Copyright 2017 Leonhard Riedißer <leo008180@googlemail.com>
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
package de.l7r7.lab.conway

import de.l7r7.lab.conway.GameOfLife.Board.Cell

import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

object GameOfLife extends App {
  case class Pos(x: Int, y: Int) {
    lazy val neighbors: Iterable[Pos] = {
      val all = for {
        neighborY <- this.y - 1 to this.y + 1
        neighborX <- this.x - 1 to this.x + 1
      } yield Pos(neighborX, neighborY)
      all.filterNot(_ == this)
    }
  }

  object Board {
    private type Cell = Char
    private val LineSeparator = System.getProperty("line.separator")
    private val Alive = 'o'
    private val Dead = ' '
    def apply(percentage: Int = 30, width: Int = 50, height: Int = 30): Board = {
      val elements = for {
        y <- 0 until height
        x <- 0 until width
      } yield {
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) Pos(x, y) -> Dead
        else Pos(x, y) -> (if (Math.random() <= percentage / 100.0) Alive else Dead)
      }
      new Board(elements.toMap, width, height)
    }
  }
  case class Board private(cells: Map[Pos, Cell], width: Int, height: Int) {
    import Board._

    override lazy val toString: String = cells.toSeq
                                         .sortWith((a: (Pos, Cell), b: (Pos, Cell)) => a._1.x.compareTo(b._1.x) < 0)
                                         .sortWith((a: (Pos, Cell), b: (Pos, Cell)) => a._1.y.compareTo(b._1.y) < 0)
                                         .map(_._2.toString)
                                         .mkString(" ")
                                         .grouped(2 * width)
                                         .mkString(LineSeparator)

    private val neighborsAlive = (neighbors: Iterable[Pos], cells: Map[Pos, Cell]) =>
      neighbors map (cells(_)) count (_ == Alive)

    private def isBorderOfBoard(width: Int, height: Int)(pos: Pos) =
      pos.x == 0 || pos.x == width - 1 || pos.y == 0 || pos.y == height - 1

    private val survives = { (pos: Pos, cells: Map[Pos, Cell]) =>
      val aliveNeighbors = neighborsAlive(pos.neighbors, cells)
      (cells(pos) == Alive && aliveNeighbors == 2) || aliveNeighbors == 3
    }

    private val next = { current: Board =>
      val isBorder: (Pos) => Boolean = isBorderOfBoard(current.width, current.height)
      val newCells = current.cells.map { case (pos: Pos, cell: Cell) =>
        if (isBorder(pos)) pos -> cell
        else if (survives(pos, current.cells)) pos -> Alive
        else pos -> Dead
      }
      current.copy(cells = newCells)
    }

    def play(iterations: Int = 10, delay: FiniteDuration = 500 millis): Unit = {
      val action = { board: Board =>
        println(board)
        Thread.sleep(delay.toMillis)
        next(board)
      }

      Stream.iterate(this)(action).take(iterations).force
    }
  }

  Board(percentage = 60).play(80)
}