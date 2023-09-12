angular.module('emixApp.directives').directive("txDataType", function () {
    return {
        restrict: 'EA',
        require: 'ngModel',
        link: function (scope, element, attr, ngModelCtrl) {
            if (attr.txDataType === 'integer') {
                ngModelCtrl.$parsers.push(integer);
            } else if (attr.txDataType === 'decimal') {
                ngModelCtrl.$parsers.push(decimal);
            } else if (attr.txDataType === 'time') {
                ngModelCtrl.$parsers.push(time);
            } else if (attr.txDataType === 'string') {
                //Checking Max length
                if (attr.txMaxLength) {
                    ngModelCtrl.$parsers.push(function (value) {
                        if (value.length > attr.txMaxLength) {
                            value = value.substr(0, attr.txMaxLength);
                            ngModelCtrl.$setViewValue(value);
                            ngModelCtrl.$render();
                        }
                        return value;
                    });
                }
            }
            else {
                //by default string
                //Checking Max length
                if (attr.txMaxLength) {
                    ngModelCtrl.$parsers.push(function (value) {
                        if (value.length > attr.txMaxLength) {
                            value = value.substr(0, attr.txMaxLength);
                            ngModelCtrl.$setViewValue(value);
                            ngModelCtrl.$render();
                        }
                        return value;
                    });
                }
            }

            function integer(text) {
                if (text) {
                    var transformedInput = text.replace(/[^0-9]/g, '');
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    if (attr.txMaxValue && (parseInt(transformedInput, 10) > parseInt(attr.txMaxValue, 10))) {
                        transformedInput = transformedInput.substring(0, transformedInput.length - 1);
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }
                return undefined;
            }

            function decimal(text) {
                if (!attr.txDecimalPoint) {
                    attr.txDecimalPoint = 2;
                }
                if (text) {
                    var transformedInput = text.replace(/[^0-9.]/g, '');

                    var match = transformedInput.match(/^[^.]*\.|[^.]+/g);
                    transformedInput = match ? match.join('') : transformedInput;
                    var n = transformedInput.split('.');
                    if (n[1]) {
                        var n2 = n[1].slice(0, attr.txDecimalPoint);
                        transformedInput = [n[0], n2].join('.');
                    }
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    if (attr.txMaxValue && (parseFloat(transformedInput, 10) > parseFloat(attr.txMaxValue, 10))) {
                        transformedInput = transformedInput.substring(0, transformedInput.length - 1);
                        transformedInput = transformedInput.replace(/[^0-9]/g, '');
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    if (attr.txMaxValue && (parseFloat(transformedInput, 10) === parseFloat(attr.txMaxValue, 10))) {
                        transformedInput = transformedInput.replace(/[^0-9]/g, '');
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }

                    if (attr.txMaxLength && transformedInput.length > attr.txMaxLength) {
                        transformedInput = transformedInput.substr(0, attr.txMaxLength);
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }

                    return transformedInput;
                }
                return undefined;
            }

            function time(text) {
                if (text) {
                    var transformedInput = text.replace(/[^0-9:]/g, '');
                    var match = transformedInput.match(/^[^:]*\:|[^:]+/g);
                    transformedInput = match ? match.join('') : transformedInput;

                    var n = transformedInput.split(':');
                    if (n[0]) {
                        var isHrValid = /^([0-1]?[0-9]|2[0-3])?$/.test(n[0]);
                        if (!isHrValid) {
                            transformedInput = '';
                        }
                        if (n[1]) {
                            if (n[1].length > 2) {
                                n[1] = n[1].slice(0, 2);
                                transformedInput = [n[0], ':', n[1]].join('');
                            }
                            transformedInput = isMinuteValid(transformedInput, n[0], n[1]);

                            if (n[1].length === 1 && n[1] > 5) {
                                n[1] = [n[1], '0'].join('');
                                transformedInput = isMinuteValid(transformedInput, n[0], n[1]);
                            }
                        }
                    }
                    else {
                        transformedInput = '00:';
                    }

                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }
                return undefined;
            }

            function isMinuteValid(input, hour, minute) {
                var isValidInput = /^([0-5]?[0-9])$/.test(minute);
                if (!isValidInput) {
                    input = [hour, ':'].join('');
                }
                return input;
            }

            element.bind('blur', function () {
                if (attr.txDataType === 'time') {
                    var text = ngModelCtrl.$viewValue;
                    if (text) {
                        var n = text.split(':');
                        if (!n[1] && n[0]) {
                            text = text + ':00';
                            ngModelCtrl.$setViewValue(text);
                            ngModelCtrl.$render();
                        }
                        else if (n[0] && n[1]) {
                            if (n[1].length === 1) {
                                text = text + '0';
                                ngModelCtrl.$setViewValue(text);
                                ngModelCtrl.$render();
                            }
                        }
                    }
                }
            });
        }
    }
});