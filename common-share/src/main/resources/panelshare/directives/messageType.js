angular.module('emixApp.directives').directive('messageType', ['$translate', function ($translate) {
    'use strict';
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var msgType = attrs.messageType || 'error';
            if(msgType == 'error')
                $(element).css('color', 'red');
        }
    };
}]);
