angular.module('emixApp.controllers').controller('promptmodal_index',
    ['$scope', 'httpServices', '$modalInstance', 'passObject', '$sce', '$translate', function ($scope, httpServices, $modalInstance, passObject, $sce, $translate) {
        'use strict';

        $scope.title = passObject.title;
        $scope.message = $sce.trustAsHtml(passObject.message);
        $scope.confirmText = passObject.confirmText ? passObject.confirmText : $translate.instant('common.verify');
        $scope.inputCaption = passObject.inputCaption ? passObject.inputCaption : '';
        $scope.inputType = passObject.inputType ? passObject.inputType : 'input';
        $scope.inputText = passObject.inputText || '';
        $scope.textareaText = passObject.inputText || '';

        $scope.required = passObject.required || false;

        setTimeout(function () {
            if (passObject.actionToCall)
                passObject.actionToCall();
        }, 2000);

        $scope.getResult = function(){
            return $scope.inputType === 'input' ? $scope.inputText : $scope.textareaText;
        }

        $scope.confirm = function () {
            var result = $scope.getResult();
            if($scope.required && result.length === 0){
                toastr.error($translate.instant('validate.requiredMessage', {'element': $scope.inputCaption}));
                return;
            }
            $modalInstance.close(result);
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

    }]);
