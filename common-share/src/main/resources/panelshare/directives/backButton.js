angular.module('emixApp.directives').directive('backbutton', ['$translate', '$filter', '$injector', function ($translate, $filter, $injector) {
    'use strict';
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var $element = $(element);
            if($element.text().trim() === "بازگشت" || $element.text().trim() === "") {
                //$(element).text($translate.instant('common.back'));
                scope.$watch(
                    function () {
                        return $filter('translate')('common.back');
                    },
                    function (newval) {
                        $element.text(newval);
                        $element.prop('title', newval);
                    }
                );
            }

            $(element).on('click', function () {
                if ($injector.has('$modalStack')) {
                    var modalStack = $injector.get('$modalStack')
                    var modalInstance = modalStack.getTop();
                    if(modalInstance && modalInstance.key){
                        modalInstance.key.dismiss('cancel');
                        return;
                    }
                    //console.log($injector.get('$modalStack'));
                }
                if(attrs.backurl)
                    window.location = attrs.backurl;
                else
                    history.back();

                setTimeout(function () {
                    $('html, body').scrollTop(0);
                }, 500);
                
                scope.$apply();
            });
        }
    };
}]);

