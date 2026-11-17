/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 85.66241685144124, "KoPercent": 14.337583148558759};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.3413109756097561, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.009059668853483286, 500, 1500, "E9.2 - Pico Extremo Consulta Ofertas"], "isController": false}, {"data": [1.0, 500, 1500, "SETUP - Login administrador"], "isController": false}, {"data": [1.0, 500, 1500, "SETUP - Login empresa"], "isController": false}, {"data": [0.06012195121951219, 500, 1500, "E5.F4 - POST Login Fase 4 (300 Usuarios)"], "isController": false}, {"data": [0.5253524945770065, 500, 1500, "E5.F1 - POST Login Fase 1 (50 Usuarios)"], "isController": false}, {"data": [0.9965, 500, 1500, "E7.2 - CRUD UPDATE Empresa"], "isController": false}, {"data": [0.997, 500, 1500, "E7.3 - CRUD DELETE/CLOSE Cerrar Oferta"], "isController": false}, {"data": [0.020648121594493835, 500, 1500, "E9.1 - Pico Extremo Login (1000 Usuarios)"], "isController": false}, {"data": [0.13530434782608697, 500, 1500, "E5.F3 - POST Login Fase 3 (200 Usuarios)"], "isController": false}, {"data": [0.23359025360517155, 500, 1500, "E6.1 - GET Masivo Ofertas Activas"], "isController": false}, {"data": [0.1694621938232162, 500, 1500, "E5.F2 - POST Login Fase 2 (100 Usuarios)"], "isController": false}, {"data": [0.99775, 500, 1500, "E7.1 - CRUD CREATE Oferta"], "isController": false}, {"data": [0.9873540856031129, 500, 1500, "E9.R2 - Recuperacion Consulta Ofertas"], "isController": false}, {"data": [0.9980842911877394, 500, 1500, "E9.R1 - Recuperacion Login"], "isController": false}, {"data": [0.26871965244058266, 500, 1500, "E6.2 - GET Masivo Directorio Empresas"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 36080, 5173, 14.337583148558759, 3090.7855598669594, 16, 12389, 1850.0, 10019.0, 10026.0, 10062.0, 75.64607365847726, 3339.6363360675887, 22.70888951110475], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["E9.2 - Pico Extremo Consulta Ofertas", 3201, 2598, 81.16213683223992, 9477.38800374882, 81, 11054, 10015.0, 10032.0, 10049.0, 10501.48, 45.72269279664043, 1716.0137446614008, 1.774785764151752], "isController": false}, {"data": ["SETUP - Login administrador", 1, 0, 0.0, 200.0, 200, 200, 200.0, 200.0, 200.0, 200.0, 5.0, 3.2958984375, 1.376953125], "isController": false}, {"data": ["SETUP - Login empresa", 1, 0, 0.0, 124.0, 124, 124, 124.0, 124.0, 124.0, 124.0, 8.064516129032258, 5.268712197580645, 2.220892137096774], "isController": false}, {"data": ["E5.F4 - POST Login Fase 4 (300 Usuarios)", 4100, 0, 0.0, 4027.83780487805, 139, 9342, 4600.0, 4904.0, 4981.95, 5090.98, 63.14200791585173, 41.25195634346172, 17.388717023701354], "isController": false}, {"data": ["E5.F1 - POST Login Fase 1 (50 Usuarios)", 3688, 0, 0.0, 752.5352494576974, 129, 2115, 759.0, 849.0, 893.0, 948.0, 60.59012946047184, 39.584762313530916, 16.68595362095025], "isController": false}, {"data": ["E7.2 - CRUD UPDATE Empresa", 2000, 0, 0.0, 220.81799999999964, 18, 787, 223.0, 330.8000000000002, 372.0, 491.9200000000001, 136.0544217687075, 122.49149659863946, 69.87670068027211], "isController": false}, {"data": ["E7.3 - CRUD DELETE/CLOSE Cerrar Oferta", 2000, 0, 0.0, 208.56549999999984, 18, 716, 210.0, 314.9000000000001, 365.0, 457.99, 136.08219364496154, 61.13067292644758, 55.54917670272845], "isController": false}, {"data": ["E9.1 - Pico Extremo Login (1000 Usuarios)", 3487, 2575, 73.84571264697448, 8831.576713507333, 173, 10243, 10014.0, 10030.0, 10038.0, 10091.119999999999, 49.57702424113173, 100.35508081858251, 3.5708573256557905], "isController": false}, {"data": ["E5.F3 - POST Login Fase 3 (200 Usuarios)", 2875, 0, 0.0, 2740.368000000002, 128, 8475, 3113.0, 4001.0, 4377.4, 6573.79999999999, 45.34700315457413, 29.62611827188486, 12.488139540615142], "isController": false}, {"data": ["E6.1 - GET Masivo Ofertas Activas", 4022, 0, 0.0, 2104.5862754848354, 28, 11291, 1846.5, 3948.100000000001, 4762.4, 6881.3099999999995, 43.73545594919641, 8240.54577230269, 16.699768824351363], "isController": false}, {"data": ["E5.F2 - POST Login Fase 2 (100 Usuarios)", 3756, 0, 0.0, 1487.8903088391894, 137, 3433, 1582.0, 1750.0, 1827.0, 1950.1799999999957, 60.899878394811516, 39.78712758411026, 16.77125557357114], "isController": false}, {"data": ["E7.1 - CRUD CREATE Oferta", 2000, 0, 0.0, 209.7834999999999, 20, 592, 212.0, 309.0, 350.9499999999998, 448.95000000000005, 135.92496941688188, 107.91699232023923, 92.25376342259072], "isController": false}, {"data": ["E9.R2 - Recuperacion Consulta Ofertas", 514, 0, 0.0, 62.36575875486381, 16, 1248, 27.0, 71.5, 120.5, 1039.050000000003, 11.320339169694966, 2132.9553119149873, 2.332608950005506], "isController": false}, {"data": ["E9.R1 - Recuperacion Login", 522, 0, 0.0, 151.89272030651344, 109, 577, 141.0, 168.0, 203.84999999999997, 418.0, 11.181321623647854, 7.304984537324622, 3.079231150262397], "isController": false}, {"data": ["E6.2 - GET Masivo Directorio Empresas", 3913, 0, 0.0, 1986.0743674929677, 27, 12389, 1665.0, 3836.9999999999995, 4770.299999999999, 7082.220000000003, 42.57703690807799, 6495.451297600622, 16.299021941373606], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Non HTTP response code: java.net.SocketTimeoutException/Non HTTP response message: Read timed out", 5173, 100.0, 14.337583148558759], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 36080, 5173, "Non HTTP response code: java.net.SocketTimeoutException/Non HTTP response message: Read timed out", 5173, "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["E9.2 - Pico Extremo Consulta Ofertas", 3201, 2598, "Non HTTP response code: java.net.SocketTimeoutException/Non HTTP response message: Read timed out", 2598, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["E9.1 - Pico Extremo Login (1000 Usuarios)", 3487, 2575, "Non HTTP response code: java.net.SocketTimeoutException/Non HTTP response message: Read timed out", 2575, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
