/**
 * A generic confirmation for risky actions.
 * Usage: Add attributes:
 * * ng-confirm-message="Are you sure?"
 * * ng-confirm-click="takeAction()" function
 * * ng-confirm-condition="mustBeEvaluatedToTrueForTheConfirmBoxBeShown" expression
 */
angular.module('emixApp.directives').directive('ngConfirmClick', function () {
    'use strict';
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('click', function() {
                //var condition = scope.$eval(attrs.ngConfirmCondition);
                //if(condition){
                    var message = attrs.ngConfirmMessage ? attrs.ngConfirmMessage : 'آیا از انجام عملیات اطمینان دارید؟';
                    if (message && confirm(message)) {
                        scope.$apply(attrs.ngConfirmClick);
                    }
                /*}
                else{
                    scope.$apply(attrs.ngConfirmClick);
                }*/
            });
        }
    }
});

