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

    var data = {"OkPercent": 86.61942359735514, "KoPercent": 13.380576402644868};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.3339424713333147, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.00940144155437167, 500, 1500, "E9.2 - Pico Extremo Consulta Ofertas"], "isController": false}, {"data": [1.0, 500, 1500, "SETUP - Login administrador"], "isController": false}, {"data": [1.0, 500, 1500, "SETUP - Login empresa"], "isController": false}, {"data": [0.05408328826392644, 500, 1500, "E5.F4 - POST Login Fase 4 (300 Usuarios)"], "isController": false}, {"data": [0.5501101321585903, 500, 1500, "E5.F1 - POST Login Fase 1 (50 Usuarios)"], "isController": false}, {"data": [1.0, 500, 1500, "E7.2 - CRUD UPDATE Empresa"], "isController": false}, {"data": [1.0, 500, 1500, "E7.3 - CRUD DELETE/CLOSE Cerrar Oferta"], "isController": false}, {"data": [0.02181450447847443, 500, 1500, "E9.1 - Pico Extremo Login (1000 Usuarios)"], "isController": false}, {"data": [0.08863920099875156, 500, 1500, "E5.F3 - POST Login Fase 3 (200 Usuarios)"], "isController": false}, {"data": [0.19161499852376734, 500, 1500, "E6.1 - GET Masivo Ofertas Activas"], "isController": false}, {"data": [0.15299479166666666, 500, 1500, "E5.F2 - POST Login Fase 2 (100 Usuarios)"], "isController": false}, {"data": [1.0, 500, 1500, "E7.1 - CRUD CREATE Oferta"], "isController": false}, {"data": [1.0, 500, 1500, "E9.R2 - Recuperacion Consulta Ofertas"], "isController": false}, {"data": [1.0, 500, 1500, "E9.R1 - Recuperacion Login"], "isController": false}, {"data": [0.22, 500, 1500, "E6.2 - GET Masivo Directorio Empresas"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 35843, 4796, 13.380576402644868, 3086.766565298651, 13, 12323, 1614.0, 10019.0, 10028.0, 10150.990000000002, 72.72111231265838, 2524.555408401943, 21.73500100429514], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["E9.2 - Pico Extremo Consulta Ofertas", 3191, 2445, 76.62174866812911, 9529.744280789717, 173, 12323, 10014.0, 10054.0, 10195.0, 11569.119999999997, 45.31125752584346, 1784.7942129991195, 2.1827331147762123], "isController": false}, {"data": ["SETUP - Login administrador", 1, 0, 0.0, 218.0, 218, 218, 218.0, 218.0, 218.0, 218.0, 4.587155963302752, 3.02376003440367, 1.263259747706422], "isController": false}, {"data": ["SETUP - Login empresa", 1, 0, 0.0, 123.0, 123, 123, 123.0, 123.0, 123.0, 123.0, 8.130081300813009, 5.311547256097561, 2.238948170731707], "isController": false}, {"data": ["E5.F4 - POST Login Fase 4 (300 Usuarios)", 3698, 4, 0.10816657652785289, 4487.930773390999, 145, 10018, 5069.5, 5586.0, 5825.049999999999, 8582.119999999997, 55.64081730913906, 36.46300588118022, 15.306385133610183], "isController": false}, {"data": ["E5.F1 - POST Login Fase 1 (50 Usuarios)", 3632, 0, 0.0, 763.8540748898668, 139, 2730, 793.0, 885.7000000000003, 921.3499999999999, 1345.0100000000002, 59.71032600654315, 39.009968846071644, 16.443663997895673], "isController": false}, {"data": ["E7.2 - CRUD UPDATE Empresa", 2000, 0, 0.0, 25.173999999999946, 13, 67, 24.0, 33.0, 37.0, 45.99000000000001, 125.01562695336918, 112.55313164145518, 64.20724465558195], "isController": false}, {"data": ["E7.3 - CRUD DELETE/CLOSE Cerrar Oferta", 2000, 0, 0.0, 23.272500000000004, 14, 58, 22.0, 30.0, 32.0, 39.99000000000001, 125.23481527864746, 56.257827175954915, 51.12124295554164], "isController": false}, {"data": ["E9.1 - Pico Extremo Login (1000 Usuarios)", 3461, 2347, 67.81277087546951, 8882.290089569486, 161, 10230, 10012.0, 10031.0, 10047.0, 10115.380000000001, 49.29637648131267, 94.26587782019143, 4.3696609538799], "isController": false}, {"data": ["E5.F3 - POST Login Fase 3 (200 Usuarios)", 4005, 0, 0.0, 2710.7852684144837, 140, 6408, 3049.0, 3299.0, 3386.0, 3439.0, 63.15540487266419, 41.260708847473, 17.39240642001104], "isController": false}, {"data": ["E6.1 - GET Masivo Ofertas Activas", 3387, 0, 0.0, 2066.2911130794205, 103, 8543, 1885.0, 3564.600000000001, 4248.0, 5594.839999999999, 36.73217074440396, 5886.796688177138, 14.025662852599558], "isController": false}, {"data": ["E5.F2 - POST Login Fase 2 (100 Usuarios)", 3840, 0, 0.0, 1454.1197916666677, 131, 4268, 1540.0, 1693.0, 1746.9499999999998, 1890.1800000000003, 62.40148203519834, 40.76815574369891, 17.184783138599542], "isController": false}, {"data": ["E7.1 - CRUD CREATE Oferta", 2000, 0, 0.0, 24.131999999999966, 14, 59, 23.0, 31.0, 33.0, 41.0, 124.74271814382836, 99.03889633880121, 84.664247177696], "isController": false}, {"data": ["E9.R2 - Recuperacion Consulta Ofertas", 671, 0, 0.0, 32.715350223546935, 14, 457, 23.0, 44.0, 76.0, 245.0, 11.507854839816149, 1844.279833893934, 2.371247432813679], "isController": false}, {"data": ["E9.R1 - Recuperacion Login", 681, 0, 0.0, 150.71365638766517, 119, 477, 144.0, 168.0, 190.69999999999993, 290.61999999999955, 11.495222983694001, 7.510062671964147, 3.165676641993856], "isController": false}, {"data": ["E6.2 - GET Masivo Directorio Empresas", 3275, 0, 0.0, 1956.6836641221382, 106, 11908, 1779.0, 3443.0000000000005, 4165.399999999999, 5526.079999999985, 35.60595353287163, 4859.795399969014, 13.630404086802422], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Non HTTP response code: java.net.SocketTimeoutException/Non HTTP response message: Read timed out", 4796, 100.0, 13.380576402644868], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 35843, 4796, "Non HTTP response code: java.net.SocketTimeoutException/Non HTTP response message: Read timed out", 4796, "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["E9.2 - Pico Extremo Consulta Ofertas", 3191, 2445, "Non HTTP response code: java.net.SocketTimeoutException/Non HTTP response message: Read timed out", 2445, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["E5.F4 - POST Login Fase 4 (300 Usuarios)", 3698, 4, "Non HTTP response code: java.net.SocketTimeoutException/Non HTTP response message: Read timed out", 4, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["E9.1 - Pico Extremo Login (1000 Usuarios)", 3461, 2347, "Non HTTP response code: java.net.SocketTimeoutException/Non HTTP response message: Read timed out", 2347, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
