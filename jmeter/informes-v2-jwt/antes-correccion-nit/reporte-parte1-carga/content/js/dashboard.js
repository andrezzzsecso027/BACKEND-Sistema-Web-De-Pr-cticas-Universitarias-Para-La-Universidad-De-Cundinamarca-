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

    var data = {"OkPercent": 99.72730814179977, "KoPercent": 0.2726918582002337};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9912349045578497, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "SETUP - Login administrador"], "isController": false}, {"data": [1.0, 500, 1500, "SETUP - Login empresa"], "isController": false}, {"data": [1.0, 500, 1500, "E2.4 - GET Admin Comentarios Pendientes (/api/admin/comentarios/pendientes)"], "isController": false}, {"data": [1.0, 500, 1500, "E2.1 - GET Ofertas Estudiantes (/api/ofertas/estudiantes)"], "isController": false}, {"data": [1.0, 500, 1500, "E2.3 - GET Admin Todos los Estudiantes (/api/admin/estudiantes)"], "isController": false}, {"data": [0.3, 500, 1500, "E3.2 - POST Registro mismo NIT (esperado: 1x201 y 9x409)"], "isController": false}, {"data": [1.0, 500, 1500, "E2.2 - GET Admin Todas las Empresas (/api/admin/empresas/todas)"], "isController": false}, {"data": [0.5, 500, 1500, "E3.1 - POST Registro Concurrente de Empresas (/api/registro/empresas)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.3 - Registro de Nueva Oferta (/api/ofertas/crear)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.4 - Actualizacion Perfil Empresa (/api/registro/empresas/nit/{nit}/perfil)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.5 - Consulta Final: la oferta creada persiste (/api/admin/ofertas)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.1 - Login Empresa (/api/auth/login)"], "isController": false}, {"data": [1.0, 500, 1500, "E4.2 - Consulta de Ofertas (/api/ofertas/estudiantes)"], "isController": false}, {"data": [0.9966666666666667, 500, 1500, "E1 - POST Login (/api/auth/login)"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2567, 7, 0.2726918582002337, 55.36112193221654, 9, 1157, 29.0, 133.0, 150.0, 750.3200000000002, 26.95918838875003, 1847.5164084817209, 10.666795127365624], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["SETUP - Login administrador", 1, 0, 0.0, 198.0, 198, 198, 198.0, 198.0, 198.0, 198.0, 5.050505050505051, 3.329190340909091, 1.3908617424242424], "isController": false}, {"data": ["SETUP - Login empresa", 1, 0, 0.0, 128.0, 128, 128, 128.0, 128.0, 128.0, 128.0, 7.8125, 5.10406494140625, 2.1514892578125], "isController": false}, {"data": ["E2.4 - GET Admin Comentarios Pendientes (/api/admin/comentarios/pendientes)", 500, 0, 0.0, 16.863999999999994, 9, 51, 15.0, 24.0, 28.0, 38.0, 16.703414177857955, 7.063064784191889, 6.524771163225763], "isController": false}, {"data": ["E2.1 - GET Ofertas Estudiantes (/api/ofertas/estudiantes)", 500, 0, 0.0, 45.7, 19, 156, 37.5, 80.80000000000007, 95.0, 116.0, 16.656672663068825, 2200.5676802251983, 6.360116221933507], "isController": false}, {"data": ["E2.3 - GET Admin Todos los Estudiantes (/api/admin/estudiantes)", 500, 0, 0.0, 21.49000000000002, 11, 55, 19.0, 32.0, 38.0, 51.0, 16.690032712464117, 10.235684124440885, 6.340256567527873], "isController": false}, {"data": ["E3.2 - POST Registro mismo NIT (esperado: 1x201 y 9x409)", 10, 7, 70.0, 264.5, 175, 292, 292.0, 292.0, 292.0, 292.0, 34.24657534246575, 19.828900898972606, 23.98263591609589], "isController": false}, {"data": ["E2.2 - GET Admin Todas las Empresas (/api/admin/empresas/todas)", 500, 0, 0.0, 43.41199999999997, 20, 155, 35.0, 73.0, 87.89999999999998, 130.95000000000005, 16.67389201987528, 2007.2337335762165, 6.382974288858506], "isController": false}, {"data": ["E3.1 - POST Registro Concurrente de Empresas (/api/registro/empresas)", 30, 0, 0.0, 947.6999999999999, 737, 1157, 950.5, 1147.7, 1156.45, 1157.0, 25.55366269165247, 24.016450170357754, 17.97740097955707], "isController": false}, {"data": ["E4.3 - Registro de Nueva Oferta (/api/ofertas/crear)", 75, 0, 0.0, 25.28, 16, 59, 23.0, 35.400000000000006, 48.40000000000002, 59.0, 2.970414669887916, 2.3583468033387462, 2.0160529253633808], "isController": false}, {"data": ["E4.4 - Actualizacion Perfil Empresa (/api/registro/empresas/nit/{nit}/perfil)", 75, 0, 0.0, 25.2, 16, 66, 21.0, 35.80000000000001, 52.80000000000001, 66.0, 2.9549663133840274, 2.66466395876837, 1.5219230797643906], "isController": false}, {"data": ["E4.5 - Consulta Final: la oferta creada persiste (/api/admin/ofertas)", 75, 0, 0.0, 87.43999999999997, 46, 283, 75.0, 136.4, 156.60000000000005, 283.0, 2.9754820280885506, 1505.5697304213284, 1.1187115047012617], "isController": false}, {"data": ["E4.1 - Login Empresa (/api/auth/login)", 75, 0, 0.0, 150.1466666666667, 126, 215, 144.0, 178.40000000000003, 191.20000000000005, 215.0, 2.9965240321227378, 1.957690017080187, 1.2688406448519718], "isController": false}, {"data": ["E4.2 - Consulta de Ofertas (/api/ofertas/estudiantes)", 75, 0, 0.0, 34.02666666666667, 21, 78, 31.0, 52.20000000000002, 59.80000000000001, 78.0, 2.9847182425978986, 433.4737783175143, 1.139672688335721], "isController": false}, {"data": ["E1 - POST Login (/api/auth/login)", 150, 0, 0.0, 152.13333333333338, 123, 501, 147.0, 160.0, 173.0, 435.72000000000116, 7.856693903205531, 5.132937716059082, 2.163659844437461], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["500", 7, 100.0, 0.2726918582002337], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 2567, 7, "500", 7, "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["E3.2 - POST Registro mismo NIT (esperado: 1x201 y 9x409)", 10, 7, "500", 7, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
