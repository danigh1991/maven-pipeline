angular.module('emixApp.directives').directive('moneyBox', ['$parse', '$rootScope', function($parse, $rootScope) {
    'use strict';
    return {
        'require': '?ngModel',
        'restrict': 'A',
        'compile': compile
    };

    function compile(tElem, tAttrs) {

        var isInputText = tElem.is('input:text');

        return function(scope, elem, attrs, controller) {
            scope.angularCurrency =  {
                aSign: '',
                mDec: $rootScope.shopFractionSize
            };
            scope.variableOptions = false;
            var updateElement = function (newVal) {
                elem.autoNumeric('set', newVal);
            };

            elem.autoNumeric('init', scope.angularCurrency);
            if (scope.variableOptions === true) {
                scope.$watch('angularCurrency', function(newValue) {
                    elem.autoNumeric('update', newValue);
                });
            }

            if (controller && isInputText) {
                /*scope.$watch(tAttrs.ngModel, function () {
                    controller.$render();
                });*/

                controller.$render = function () {
                    updateElement(controller.$viewValue);
                };

                elem.on('keyup', function () {
                    scope.$applyAsync(function () {
                        controller.$setViewValue(elem.autoNumeric('get'));
                    });
                });
                elem.on('change', function () {
                    scope.$applyAsync(function () {
                        controller.$setViewValue(elem.autoNumeric('get'));
                    });
                });
            } else {
                if (isInputText) {
                    attrs.$observe('value', function (val) {
                        updateElement(val);
                    });
                }
            }
        };
    }
}]);