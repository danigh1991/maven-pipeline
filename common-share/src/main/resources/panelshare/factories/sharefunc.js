angular.module('emixApp.factories').factory('shareFunc', ['$modal', '$rootScope', '$translate', 'sharedInfo', '$http', '$templateCache',
    function ($modal, $rootScope, $translate, sharedInfo, $http, $templateCache) {
        //#region Private Func
        var version = $rootScope.version;
        var urlTrailSlash = function (url) {
            return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        }
        var addModalCloseButton = function (template){
            var $modal = $('<div id="modalcnt">' + template + '</div>');
            var $modalHeader = $('.content-header', $modal);
            $modalHeader.append('<button type="button" class="close" ng-click="$dismiss()">&times;</button>');
            return $modal.html();
        }
        //#endregion
        //#region Public Func
        return {
            openModal: function (passObject, templateUrl, controller, size, successFunc, dismissFunc, side) {
                sharedInfo.setNeedRefresh(false);
                $http.get(angular.isFunction(templateUrl) ? (templateUrl)() : templateUrl + version, {cache: $templateCache}).then(function (result) {
                    var template = addModalCloseButton(result.data);
                    if(side){
                        side = side === 'start' ? ($rootScope.isLeftToRight ? 'left' : 'right') : (side === 'end' ? ($rootScope.isLeftToRight ? 'right' : 'left') : side)
                    }
                    var dialogInst = $modal.open({
                        template: template,
                        controller: controller,
                        size: size || 'lg',
                        windowClass: side || '',
                        backdrop: 'static',
                        keyboard: false,//true
                        resolve: {
                            passObject: function () {
                                return passObject || {};
                            }
                        }
                    });
                    setTimeout(function () {
                        var firstInput = $('.modal-content .form-control');
                        firstInput.length > 0 ? firstInput[0].focus() : $('.modal-content button').focus();
                    }, 500);
                    dialogInst.result.then(function (result) {
                        if (typeof successFunc == 'function') {
                            successFunc(result);
                        }
                    }, function () {
                        if (typeof dismissFunc == 'function') {
                            dismissFunc();
                        }
                    });
                });
            },
            dataURItoBlob: function (dataURI) {
                var binary = atob(dataURI.split(',')[1]);
                var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
                var array = [];
                for (var i = 0; i < binary.length; i++) {
                    array.push(binary.charCodeAt(i));
                }
                return new Blob([new Uint8Array(array)], {type: mimeString});
            },
            cloneArray: function (source) {
                var destination = [];
                for (var i = 0; i < source.length; i++) {
                    destination.push(angular.copy(source[i]));
                }
                return destination;

            },
            validateProductPackages: function (productPackageAttributeList, attributeList) {
                var validate = true;
                attributeList.forEach(function (attribute, index) {
                    var currentProductPackageAttributeList = productPackageAttributeList.find(function (item) {
                        return item.id == attribute.id
                    });
                    if (!currentProductPackageAttributeList) {
                        alert($translate.instant('shop.dynamicAttributesValidateError'));
                        validate = false;
                    }
                    if ((attribute.requierd && currentProductPackageAttributeList.type == 1 && currentProductPackageAttributeList.validValId == 0)
                        || (attribute.requierd && currentProductPackageAttributeList.type == 2 && currentProductPackageAttributeList.value.length == 0))
                        validate = false;
                });
                return validate;
            },
            getProductPackageTitle: function (attributeList, brandId, brandName, businessTypeId, businessTypeName, productName) {
                var packageTitle = '';
                productName = (businessTypeId ? businessTypeName : productName);
                productName = (brandId != 1 ? (productName + ' ' + brandName) : productName);
                attributeList.forEach(function (attribute, index) {
                    var seperator = ' | ';
                    var validValArr = (attribute.validValId.toString() || '').split('_');
                    var type = validValArr.length == 3 ? validValArr[0] : attribute.type;
                    var validValId = validValArr.length == 3 ? validValArr[1] : attribute.validValId;
                    var value = validValArr.length == 3 ? validValArr[2] : attribute.value;
                    if (attribute.attribute.useInTitle && ((type == 1 && validValId != 0) || (type == 2 && value.length > 0))) {
                        packageTitle += packageTitle.length > 0 ? seperator : productName + seperator;
                        packageTitle += (attribute.attribute.useCaption ? attribute.caption + ' ' : '') + value;
                    }
                });
                return packageTitle;
            },
            getBoolDefaultValue: function (value, defaultValue) {
                return value !== undefined ? value : defaultValue !== undefined ? defaultValue : undefined; //defaultValue ? velue !== false : velue || defaultValue;
            },
            getShipmentCostHint: function (useDefaultShipmentCost, postalShipment, pickupShipment) {
                var hint = undefined;
                if (useDefaultShipmentCost || (postalShipment && pickupShipment))
                    hint = 'shop.sendingConditionsBasedBranchSendingProfile';
                else if (postalShipment && !pickupShipment)
                    hint = 'shop.inStoreDeliveryForProductDisabled';
                else if (!postalShipment && pickupShipment)
                    hint = 'shop.sendByPostForProductDisabled';
                else if (!postalShipment && !pickupShipment)
                    hint = 'shop.sendByPostDeliveryInStoreDisabled';

                hint = hint ? hint + "" : undefined;
                return hint;
            },
            urlTrailSlash: function (url) {
                return urlTrailSlash(url);
            },
            prepareUrl: function (url) {
                if (!siteConfig.isDefaultLang) {
                    var urlStartWithSlash = url.startsWith("/");
                    url = (urlStartWithSlash ? "/" : "") + siteConfig.currentLang.split("_")[0] + (urlStartWithSlash ? "" : "/") + url;
                }
                return urlTrailSlash(url).toLowerCase();
            },
            treeNodeIsExpanded: function (storageName, node) {
                var expandedNodes = sharedInfo.getState(storageName, true) || [];
                var find = false;
                angular.forEach(expandedNodes, function (item) {
                    if (!find && item == node.id) {
                        find = item;
                    }
                });
                return find;
            },
            treeBindExpanded: function (storageName, node) {
                if (this.treeNodeIsExpanded(storageName, node))
                    node.expand = true;
            },
            treeSetExpanded: function (storageName, node) {
                var expandedNodes = sharedInfo.getState(storageName, true) || [];
                var findItem = this.treeNodeIsExpanded(storageName, node);

                if (node.expand && !findItem) {
                    expandedNodes.push(node.id);
                } else if (!node.expand && findItem)
                    expandedNodes.splice(expandedNodes.indexOf(node.id), 1);

                sharedInfo.setState(storageName, expandedNodes, true);
            },
            insertText: function (input, text) {
                var back, browser, front, pos, range, scrollPos;
                if (!input) {
                    return;
                }
                scrollPos = input.scrollTop;
                pos = 0;
                browser = (input.selectionStart || input.selectionStart === '0') ? 'ff' : (document.selection ? 'ie' : false);
                if (browser === 'ie') {
                    input.focus();
                    range = document.selection.createRange();
                    range.moveStart('character', -input.value.length);
                    pos = range.text.length;
                } else if (browser === 'ff') {
                    pos = input.selectionStart;
                }
                front = input.value.substring(0, pos);
                back = input.value.substring(pos, input.value.length);
                input.value = front + text + back;
                pos = pos + text.length;
                if (browser === 'ie') {
                    input.focus();
                    range = document.selection.createRange();
                    range.moveStart('character', -input.value.length);
                    range.moveStart('character', pos);
                    range.moveEnd('character', 0);
                    range.select();
                } else if (browser === 'ff') {
                    input.selectionStart = pos;
                    input.selectionEnd = pos;
                    input.focus();
                }
                input.scrollTop = scrollPos;
                angular.element(input).trigger('input');
                return '';
            },
            downloadXLS: function (args) {

                var data, headers;

                data = args.data || null;
                fileName = args.fileName || 'exportfile';
                if (data == null || !data.length) {
                    return null;
                }

                headers = args.headers || Object.keys(data[0]); // This array holds the HEADERS text

                var excel = $JExcel.new("Calibri light 10 #333333");			// Default font

                // excel.set is the main function to generate content:
                // 		We can use parameter notation excel.set(sheetValue,columnValue,rowValue,cellValue,styleValue)
                // 		Or object notation excel.set({sheet:sheetValue,column:columnValue,row:rowValue,value:cellValue,style:styleValue })
                // 		null or 0 are used as default values for undefined entries

                excel.set({sheet: 0, value: "Sheet 1"});
                //excel.addSheet("Sheet 2");

                var evenRow = excel.addStyle({ 																	// Style for even ROWS
                    border: "none,none,none,thin #333333"
                });													// Borders are LEFT,RIGHT,TOP,BOTTOM. Check $JExcel.borderStyles for a list of valid border styles

                var oddRow = excel.addStyle({ 																	// Style for odd ROWS
                    fill: "#ECECEC", 																			// Background color, plain #RRGGBB, there is a helper $JExcel.rgbToHex(r,g,b)
                    border: "none,none,none,thin #333333"
                });


                for (var i = 1; i < data.length; i++) excel.set({row: i, style: i % 2 == 0 ? evenRow : oddRow});					// Set style for the first 50 rows
                excel.set({row: 3, value: 30});																	// We want ROW 3 to be EXTRA TALL

                var formatHeader = excel.addStyle({ 															// Format for headers
                    border: "none,none,none,thin #333333", 													// 		Border for header
                    font: "Calibri 12 #0000AA B"
                }); 														// 		Font for headers

                var h = 0;
                headers.forEach(function (header) {																// Loop all the haders
                    var headerText = header.caption ? header.caption : header;
                    excel.set(0, h, 0, headerText, formatHeader);													// Set CELL with header text, using header format
                    excel.set(0, h, undefined, "auto");															// Set COLUMN width to auto (according to the standard this is only valid for numeric columns)
                    h++;
                });

                // Now let's write some data
                //var initDate = new Date(2000, 0, 1);
                //var endDate = new Date(2016, 0, 1);
                //var dateStyle = excel.addStyle({ 																// Format for date cells
                //    align: "R",																				// 		aligned to the RIGHT
                //    format: "yyyy.mm.dd hh:mm:ss", 															// 		using DATE mask, Check $JExcel.formats for built-in formats or provide your own
                //    font: "#00AA00"
                //}); 																		// 		in color green

                //for (var i = 1; i < 50; i++) {																			// we will fill the 50 rows
                //    excel.set(0, 0, i, "This is line " + i);															// This column is a TEXT
                //   var d = randomDate(initDate, endDate);															// Get a random date
                //    excel.set(0, 1, i, d.toLocaleString());														// Store the random date as STRING
                //    excel.set(0, 2, i, $JExcel.toExcelLocalTime(d));												// Store the previous random date as a NUMERIC (there is also $JExcel.toExcelUTCTime)
                //    excel.set(0, 3, i, $JExcel.toExcelLocalTime(d), dateStyle);										// Store the previous random date as a NUMERIC,  display using dateStyle format
                //    excel.set(0, 4, i, "Some other text");															// Some other text
                //}

                var i = 1;
                data.forEach(function (item) {
                    var j = 0;
                    headers.forEach(function (header) {
                        var headerKey = header.key ? header.key : header;
                        var value = item[headerKey] == null ? '' : item[headerKey].toString();
                        excel.set(0, j, i, value);
                        j++;
                    });
                    i++;
                });

                //excel.set(0, 1, undefined, 30);																	// Set COLUMN 1 to 30 chars width
                //excel.set(0, 3, undefined, 30);																	// Set COLUMN 3 to 20 chars width
                //excel.set(0, 4, undefined, 20, excel.addStyle({ align: "R" }));										// Align column 4 to the right
                //excel.set(0, 1, 3, undefined, excel.addStyle({ align: "L T" }));										// Align cell 1-3  to LEFT-TOP
                //excel.set(0, 2, 3, undefined, excel.addStyle({ align: "C C" }));										// Align cell 2-3  to CENTER-CENTER
                //excel.set(0, 3, 3, undefined, excel.addStyle({ align: "R B" }));										// Align cell 3-3  to RIGHT-BOTTOM

                excel.generate(fileName + ".xlsx");

            },
            downloadXLSAsHtmlTable: function (args) {
                var result, data;

                fileName = (args.fileName || 'exportfile') + '.xls';
                data = args.data || null;
                if (data == null || !data.length) {
                    return null;
                }

                headers = args.headers || Object.keys(data[0]); // This array holds the HEADERS text

                result = "<html><head><meta http-equiv=Content-Type content='text/html; charset=utf-8'>";

                result += "<style>";
                result += ".header{border: 2px solid #79A4CE;background-position: center;background-repeat: repeat-x;background-color: #1369b0;color: #ffffff;height: 30px;vertical-align: middle;font-size: 12pt;}";
                result += ".evnrow{border: 1px solid #79A4CE;background-color: #fff;color: #000000;height: 30px;vertical-align: middle;font-size: 10pt;}";
                result += ".oddrow{border: 1px solid #79A4CE;background-color: #ecf2f6;color: #000000;height: 30px;vertical-align: middle;font-size: 10pt;}";
                result += "</style>";
                result += "</head><body><table border='1'><tr>";

                headers.forEach(function (header) {
                    var headerText = header.caption ? header.caption : header;
                    result += "<th class='header'>" + headerText + "</th>";
                });
                result += "</tr>";

                data.forEach(function (item, index) {
                    var cssClass = index % 2 == 0 ? 'evnrow' : 'oddrow';
                    result += "<tr>";
                    headers.forEach(function (header) {
                        var headerKey = header.key ? header.key : header;
                        result += "<td class='" + cssClass + "'>" + item[headerKey] + "</td>";
                    });
                    result += "</tr>";
                });
                result += "</table></body></html>";
                var blob = new Blob([result], {type: "text/plain;charset=utf-8"});
                saveAs(blob, fileName);
            },
            showMenu: function () {
                var $body = $("body");
                $body.removeClass('sidebar-collapse');
                $body.addClass('sidebar-open');
                jQuery('.sidebar-toggle').show();
                jQuery('.navbar-custom-menu').show();

                $rootScope.$emit("CallUpdateInfo", {});
                $rootScope.$emit("CallUpdateMenu", {});
            },
            hideMenu: function () {
                var $body = $("body");
                $body.addClass('sidebar-collapse');
                $body.removeClass('sidebar-open');
                jQuery('.sidebar-toggle').hide();
                jQuery('.navbar-custom-menu').hide();
            },
            validateIp: function (ip) {
                var ipPattern = '^(?!0)(?!.*\\.$)((1?\\d?\\d|25[0-5]|2[0-4]\\d)(\\.|$)){4}$';
                var ipRegex = new RegExp(ipPattern);
                return ipRegex.test(ip);
            },
            getMomentDateFormat: function (format) {
                return $rootScope.calendarType === 'gregorian' ? format : 'j' + format.split(' ').join(' j');
            },
            openBasketModal: function (successFunc, dismissFunc, passObject, direct, size){
                direct = direct || 'end'
                passObject = passObject || {};
                this.openModal(passObject, '/panel/home/ngApp/pages/application/order/basket.html', 'shopbasket_index', size,
                    function (result) {
                        if (typeof successFunc == 'function') {
                            successFunc(result);
                        }
                    }, function () {
                        //dismiss Dialog
                        if (typeof dismissFunc == 'function') {
                            dismissFunc();
                        }
                    }, direct);
            },
            openReservationModal: function (successFunc, dismissFunc, passObject, direct, size){
                direct = direct || 'start';
                size = size || 'lg';
                passObject = passObject || {};
                this.openModal(passObject, '/panel/home/ngApp/pages/application/reservation/add.html', 'addreserve_index', size,
                    function (result) {
                        if (typeof successFunc == 'function') {
                            successFunc(result);
                        }
                    }, function () {
                        //dismiss Dialog
                        if (typeof dismissFunc == 'function') {
                            dismissFunc();
                        }
                    }, direct);
            },
            printHtml: function (elementSelector, isAdminPanel) {
                isAdminPanel = isAdminPanel === true;
                var divToPrint = $(elementSelector).clone();
                var newWin = window.open('', 'Print-Window');
                newWin.document.open();
                newWin.document.write('<html dir="' + (siteConfig.isLeftToRight ? 'ltr' : 'rtl') + '"><head><link href="/' + (isAdminPanel ? 'nimdalenap' : 'panel') + '/Content/style.css'+ siteConfig.resourceVersion +'" rel="stylesheet"></head><body class="'+(siteConfig.isLeftToRight ? 'mbdltr' : '')+'" onload="setTimeout(function () {window.print()},1000);">' + $('<div>').append(divToPrint).html() + '</body></html>');
                newWin.document.close();
            }
        }
        //#endregion
    }]);
