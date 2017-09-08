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
    let result = "<div class='wrapper'>";
    let k = 0;
    for (let i = 0; i < height; i++) {
        for (let j = 0; j < width; j++) {
            if (cells[k]) {
                result += "<div style='float:left' class='alive'></div>";
            } else {
                result += "<div style='float:left' class='dead'></div>";
            }
            k += 1;
        }
        result += "<div/>";
    }
    result += "<br>";
    return result;
}

$(function () {
    const wsUrl = 'ws://127.0.0.1:4567/game-of-life/';
    console.log("Connecting to WebSocket server at '" + wsUrl + "'");
    const webSocket = new WebSocket(wsUrl);

    const dataSource = Rx.Observable.fromEvent(webSocket, 'message')
        .map(msg => msg.data);

    dataSource.subscribe(payload => {
        const parsed = JSON.parse(payload);
        const table = generateBoardDOM(parsed.height, parsed.width, parsed.cells);
        $('#content').html(table);
    });
});