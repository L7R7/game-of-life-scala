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

function generateBoardDOM(height, width, cells) {
    let table = "<table>";
    let k = 0;
    for (let i = 0; i < height; i++) {
        table += "<tr>";
        for (let j = 0; j < width; j++) {
            if (cells[k]) {
                table += "<td class='alive'></td>";
            } else {
                table += "<td class='dead'></td>";
            }
            k += 1;
        }
        table += "</tr>";
    }
    table += "</table>";
    return table;
}

$(function () {
    const wsUrl = 'ws://127.0.0.1:4567/game-of-life/';
    console.log("Connecting to WebSocket server at '" + wsUrl + "'");
    const webSocket = new WebSocket(wsUrl);

    const dataSource = Rx.Observable.fromEvent(webSocket, 'message')
        .map(msg => msg.data);

    dataSource.subscribe(payloaz => {
        const payload = {
            height: 4,
            width: 8,
            cells: [true, false, true, true, true, true, true, false, true, true, false, true, false, true, true, true,true, false, true, false, true, false, true, false, true, true, false, true, false, true, true, true]
        };
        const table = generateBoardDOM(payload.height, payload.width, payload.cells);
        $('#content').html(table);
    });
    dataSource.subscribe(payloaz => console.log("Received message '" + payloaz + "' from server"));
});