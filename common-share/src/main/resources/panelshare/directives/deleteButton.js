angular.module('emixApp.directives').directive('deletebutton', ['$translate', function ($translate) {
    'use strict';
    return {
        priority: -100,
        restrict: 'A',
        link: function (scope, element, attrs) {
            var $element = $(element);
            var btnText = '';
            if($element.text().trim() === "حذف" || $(element).text().trim() === "") {
                btnText = $translate.instant('common.delete');
                if($element.html().length === 0)
                    $element.text(btnText);
                $element.prop('title', btnText);
            }

            element.bind('click', function (e) {
                var message = attrs.ngConfirmClick || $translate.instant("common.deleteConfirm");
                if (!confirm(message)) {
                    e.stopImmediatePropagation();
                    e.preventDefault();
                }
            });
        }
    };
}]);
