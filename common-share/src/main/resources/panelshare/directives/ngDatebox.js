angular.module('emixApp.directives').directive('ngdatebox', function () {
    'use strict';
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            $(element).attr('autocomplete','off');
            $(element).on('click', function () {
                PersianDatePicker.Show(this, this.value);
                scope.$apply();
            });

            $(element).focus(function () {
                PersianDatePicker.Show(this, this.value);
                scope.$apply();
            });
            
            $(element).focusout(function () {
                setTimeout(function () {
                    scope.$emit('changeDate', element, attrs);
                }, 1);
            })
            
        }
    };
});
