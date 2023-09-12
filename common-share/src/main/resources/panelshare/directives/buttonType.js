angular.module('emixApp.directives').directive('buttonType', ['$translate', '$filter', function ($translate, $filter) {
    'use strict';
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {

            var $element = $(element);
            var btnType = attrs.buttonType || 'back';
            var btnKey = '';
            var btnText = '';
            if(btnType === 'ok' || $element.text().trim() === "ثبت" && $element)
                btnKey = 'common.save';

            if(btnType === 'back' || $element.text().trim() === "بازگشت")
                btnKey = 'common.back';

            if(btnType === 'cancel' || $element.text().trim() === "انصراف")
                btnKey = 'common.cancel';

            if(btnType === 'delete' || $element.text().trim() === "حذف")
                btnKey = 'common.delete';

            if(btnType === 'new' || $element.text().trim() === "جدید")
                btnKey = 'common.new';

            if(btnType === 'edit' || $element.text().trim() === "ویرایش")
                btnKey = 'common.edit';

            if(btnType === 'approve' || $element.text().trim() === "تایید")
                btnKey = 'common.verify';

            if(btnType === 'details' || $element.text().trim() === "جزئیات")
                btnKey = 'common.details';

            if(btnType === 'print' || $element.text().trim() === "چاپ")
                btnKey = 'common.print';

            if($element.attr('button-text') === undefined) {
                scope.$watch(
                    function () {
                        return $filter('translate')(btnKey);
                    },
                    function (newval) {
                        btnText = newval;
                        $element.text(btnText);
                        $element.prop('title', btnText);
                    }
                );
            }
            /*$element.text(btnText);
            $element.prop('title', btnText);*/
        }
    };
}]);

angular.module('emixApp.directives').directive('buttonText', ['$translate', '$filter', function ($translate, $filter) {
    'use strict';
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {

            var $element = $(element);
            var btnTextKey = attrs.buttonText;
            var btnText = $translate.instant(btnTextKey);

            scope.$watch(
                function() { return $filter('translate')(btnTextKey); },
                function(newval) {
                    btnText = newval;
                    $element.text(btnText);
                    $element.prop('title', btnText);
                }
            );
            //$element.text(btnText);
            //$element.prop('title', btnText);
        }
    };
}]);
