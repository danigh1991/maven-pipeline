/**
 * A generic confirmation for risky actions.
 * Usage: Add attributes:
 * * ng-confirm-message="Are you sure?"
 * * ng-confirm-click="takeAction()" function
 * * ng-confirm-condition="mustBeEvaluatedToTrueForTheConfirmBoxBeShown" expression
 */
/*
angular.module('emixApp.directives').directive('ngCompile',['$compile', function ($compile) {
    'use strict';
    return function(scope, element, attrs) {
        scope.$watch(
            function(scope) {
                return scope.$eval(attrs.ngCompile);
            },
            function(value) {
                element.html(value);
                $compile(element.contents())(scope);
            }
        )};
}]);

*/
