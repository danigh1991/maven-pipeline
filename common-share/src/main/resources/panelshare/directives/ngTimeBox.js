//https://jsfiddle.net/gengns/xcgjz9xt/
//angular.module("/angular-time-box.tpl.html", []).run(["$templateCache", function ($templateCache) {
//    $templateCache.put("/angular-time-box.tpl.html",
//      '<div class="dvtimebox">'
//        + '<input type="number" value="timeHour" min="0" max="23" placeholder="23">:'
//        + '<input type="number" value="timeMinuts" min="0" max="59" placeholder="00">'
//    + '</div>');
//}]);

//angular.module('emixApp.directives')
//    .directive('ng-TimeBox', function () {
//        return {
//            restrict: 'EA', //E = element, A = attribute, C = class, M = comment         
//            replace: true,
//            templateUrl: '/angular-time-box.tpl.html',
//            scope: {
//                //@ reads the attribute value, = provides two-way binding, & works with functions
//                title: '=',
//                changeHourCallback: '&',
//                changeMinutsCallback: '&',
//                enable: "=?"
//            },
            
//            controller: controllerFunction, //Embed a custom controller in the directive
//            link: function ($scope, element, attrs) {  //DOM manipulation

//            }
//        }
//    });