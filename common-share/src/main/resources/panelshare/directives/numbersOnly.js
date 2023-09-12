angular.module('emixApp.directives').directive('numbersOnly', function () {
    var convertNumbers2English= function (string) {
        return string.replace(/[\u0660-\u0669\u06f0-\u06f9]/g, function (c) {
            return c.charCodeAt(0) & 0xf;
        });
    };
    return {
        require: 'ngModel',
        link: function (scope, element, attr, ngModelCtrl) {
            function fromUser(text) {
                if (text) {
                    text = convertNumbers2English(text);
                    //alert(text);
                    var transformedInput = text.replace(/[^0-9]/g, '');
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    
                    
                    return transformedInput;
                }
                return '';
            }            
            ngModelCtrl.$parsers.push(fromUser);
        },
    };
});

angular.module('emixApp.directives').directive('englishNumbers', function () {
    var convertNumbers2English= function (string) {
        return string.replace(/[\u0660-\u0669\u06f0-\u06f9]/g, function (c) {
            return c.charCodeAt(0) & 0xf;
        });
    };
    return {
        require: 'ngModel',
        link: function (scope, element, attr, ngModelCtrl) {
            function fromUser(text) {
                if (text) {
                    text = convertNumbers2English(text);
                    return text;
                }
                return '';
            }
            ngModelCtrl.$parsers.push(fromUser);
        },
    };
});
