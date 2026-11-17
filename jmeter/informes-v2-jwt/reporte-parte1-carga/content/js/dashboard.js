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

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9939618231398519, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.5, 500, 1500, "SETUP - Login administrador"], "isController": false}, {"data": [1.0, 500, 1500, "SETUP - Login empresa"], "isController": false}, {"data": [1.0, 500, 1500, "E2.4 - GET Admin Comentarios Pendientes (/api/admin/comentarios/pendientes)"], "isController": false}, {"data": [1.0, 500, 1500, "E2.1 - GET Ofertas Estudiantes (/api/ofertas/estudiantes)"], "isController": false}, {"data": [1.0, 500, 1500, "E2.3 - GET Admin Todos los Estudiantes (/api/admin/estudiantes)"], "isController": false}, {"data": [1.0, 500, 1500, "E3.2 - POST Registro mismo NIT (esperado: 1x201 y 9x409)"], "isController": false}, {"data": [1.0, 500, 1500, "E2.2 - GET Admin Todas las Empresas (/api/admin/empresas/todas)"], "isController": false}, {"data": [0.5, 500, 1500, "E3.1 - POST Registro Concurrente de Empresas (/api/registro/empresas)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.3 - Registro de Nueva Oferta (/api/ofertas/crear)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.4 - Actualizacion Perfil Empresa (/api/registro/empresas/nit/{nit}/perfil)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.5 - Consulta Final: la oferta creada persiste (/api/admin/ofertas)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.1 - Login Empresa (/api/auth/login)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.2 - Consulta de Ofertas (/api/ofertas/estudiantes)"], "isController": false}, {"data": [1.0, 500, 1500, "E1 - POST Login (/api/auth/login)"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2567, 0, 0.0, 57.20529801324499, 9, 1026, 30.0, 140.0, 156.0, 656.6400000000003, 26.220633299284987, 2644.1687348378446, 10.37457506064862], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["SETUP - Login administrador", 1, 0, 0.0, 930.0, 930, 930, 930.0, 930.0, 930.0, 930.0, 1.075268817204301, 0.7087953629032258, 0.2961189516129032], "isController": false}, {"data": ["SETUP - Login empresa", 1, 0, 0.0, 119.0, 119, 119, 119.0, 119.0, 119.0, 119.0, 8.403361344537815, 5.4900866596638656, 2.3142069327731094], "isController": false}, {"data": ["E2.4 - GET Admin Comentarios Pendientes (/api/admin/comentarios/pendientes)", 500, 0, 0.0, 15.975999999999999, 9, 63, 15.0, 22.0, 25.94999999999999, 35.0, 17.019538430117777, 7.196738418204098, 6.648257199264756], "isController": false}, {"data": ["E2.1 - GET Ofertas Estudiantes (/api/ofertas/estudiantes)", 500, 0, 0.0, 49.06399999999994, 20, 280, 40.0, 79.90000000000003, 100.94999999999999, 142.98000000000002, 16.777397490101333, 2688.7909420928127, 6.406213299442991], "isController": false}, {"data": ["E2.3 - GET Admin Todos los Estudiantes (/api/admin/estudiantes)", 500, 0, 0.0, 20.259999999999998, 11, 83, 18.0, 28.0, 33.0, 56.950000000000045, 16.99350847976073, 10.421800122353261, 6.455541795534106], "isController": false}, {"data": ["E3.2 - POST Registro mismo NIT (esperado: 1x201 y 9x409)", 10, 0, 0.0, 242.7, 161, 252, 252.0, 252.0, 252.0, 252.0, 39.682539682539684, 19.515749007936506, 27.789403521825395], "isController": false}, {"data": ["E2.2 - GET Admin Todas las Empresas (/api/admin/empresas/todas)", 500, 0, 0.0, 43.68399999999999, 21, 166, 37.0, 72.0, 86.94999999999999, 126.96000000000004, 16.919328641039524, 2309.273563337507, 6.4769304953979425], "isController": false}, {"data": ["E3.1 - POST Registro Concurrente de Empresas (/api/registro/empresas)", 30, 0, 0.0, 835.4999999999999, 647, 1026, 849.5, 1011.9, 1019.95, 1026.0, 28.846153846153847, 27.110877403846153, 20.293719951923077], "isController": false}, {"data": ["E4.3 - Registro de Nueva Oferta (/api/ofertas/crear)", 75, 0, 0.0, 22.640000000000008, 16, 50, 22.0, 29.0, 31.400000000000006, 50.0, 2.80562621577136, 2.227513782638785, 1.9042091991807573], "isController": false}, {"data": ["E4.4 - Actualizacion Perfil Empresa (/api/registro/empresas/nit/{nit}/perfil)", 75, 0, 0.0, 22.906666666666663, 15, 67, 21.0, 30.400000000000006, 35.40000000000002, 67.0, 2.817113022574466, 2.540353676802013, 1.450923250103294], "isController": false}, {"data": ["E4.5 - Consulta Final: la oferta creada persiste (/api/admin/ofertas)", 75, 0, 0.0, 182.53333333333325, 92, 392, 165.0, 274.60000000000025, 339.6, 392.0, 2.8123593820308987, 3624.701296673448, 1.0573812129706015], "isController": false}, {"data": ["E4.1 - Login Empresa (/api/auth/login)", 75, 0, 0.0, 146.48000000000005, 126, 200, 144.0, 167.4, 174.60000000000002, 200.0, 2.8374697336561745, 1.8537766131015436, 1.2014910903450364], "isController": false}, {"data": ["E4.2 - Consulta de Ofertas (/api/ofertas/estudiantes)", 75, 0, 0.0, 38.28000000000001, 23, 116, 33.0, 53.80000000000001, 58.000000000000014, 116.0, 2.803004821168292, 485.8137298183653, 1.0702879737078148], "isController": false}, {"data": ["E1 - POST Login (/api/auth/login)", 150, 0, 0.0, 152.33333333333334, 122, 338, 144.0, 173.40000000000003, 212.79999999999995, 337.49, 7.372094166216149, 4.816338864451762, 2.030205619993119], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 2567, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
