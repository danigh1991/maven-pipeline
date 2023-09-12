angular.module('emixApp.directives').directive('myPattern', function () {
    'use strict';
    return {
        require: 'ngModel',
        link: function (scope, el, attrs, ngModel) {

            var validate = function (data) {
                if (!data) {
                    ngModel.$setValidity('pattern', true);
                    return data;
                }

                var regex = new RegExp(attrs.myPattern);
                if (regex) {
                    ngModel.$setValidity('pattern', regex.test(data));
                }
                return data;
            };

            ngModel.$formatters.push(validate);
            ngModel.$parsers.push(validate);

            //scope.$watch(attrs.myValidRec, function () {
            //    validate(scope.$eval(attrs.ngModel));
            //})
        }
    }
});