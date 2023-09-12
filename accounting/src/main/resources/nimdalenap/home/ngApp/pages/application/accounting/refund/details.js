var editPayDate = function () {
    var currScope = angular.element(document.getElementById('payDate')).scope();
    currScope.result.payDate = document.getElementById('payDate').value;
    currScope.$apply();
}
angular.module('emixApp.controllers').controller('detailsrefund_index',
 ['$scope', 'httpServices', '$rootScope', 'sharedInfo', 'shareFunc', '$filter', '$modalInstance', 'passObject', '$translate', function ($scope, httpServices, $rootScope, sharedInfo, shareFunc, $filter, $modalInstance, passObject, $translate) {
     'use strict';

     //#region properties
     $scope.showBreadcrump = !passObject;
     $scope.showError = false;

     /*$scope.payBanks = [{
         id	: 2,
         name :	'ملت',
         eName : 'MELLAT'
     }];*/
     //#endregion

     function getAccountInfo() {
         httpServices.getAccountInfo($scope.result.accountId)
             .then(function (response) {
                 var data = response.data;
                 $scope.accountInfo = data.returnObject;

             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
             })
             .finally(function () {
                 NProgress.done();
                 $.unblockUI();
             });
     }

     function getRequestRefundMoneyInfo(id) {
         NProgress.start();
         $.blockUI();

         httpServices.getRequestRefundMoneyInfo(id)
             .then(function (response) {
                 var data = response.data;
                 $scope.result = data.returnObject;
                 /*$scope.result.statusTo.unshift({
                     id	 : $scope.result.status,
                     name : '',
                     caption : $scope.result.statusDesc
                 })*/
                 //$scope.result.payBankId = $scope.result.payBankId ? $scope.result.payBankId : $scope.payBanks[0].id;
                 /*if($scope.result.accountInfo) {
                     if ($scope.result.toBankId == null && $scope.result.accountInfo.bank)
                         $scope.result.toBankId = $scope.result.accountInfo.bank.id;
                     if ($scope.result.toAccountNumber == null && $scope.result.accountInfo.acountNumber)
                         $scope.result.toAccountNumber = $scope.result.accountInfo.acountNumber;
                 }*/
                 getAccountInfo();
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
                 NProgress.done();
                 $.unblockUI();
             })
             .finally(function () {

             });
     }
     passObject && getRequestRefundMoneyInfo(passObject.id);

     var validateForm = function(){
         var hasError = false;
         $scope.payDateShowError = $scope.newStatus === 2 && !$scope.result.payDate;
         $scope.payBankShowError = $scope.newStatus === 2 && !$scope.result.payBankId;
         $scope.payBankRefShowError = $scope.newStatus === 2 && !$scope.result.payBankRef;
         $scope.toBankShowError = !$scope.result.toBankId && $scope.result.financeDestName === 'account_number';
         $scope.toAccountNumberShowError = !$scope.result.financeDestValue || $scope.result.financeDestValue.length === 0;
         $scope.toAccountNumberShowMaxError = ($scope.result.financeDestValue && $scope.result.financeDestValue.length > 35);
         /*$scope.toAccountNumberShowMinError = ($scope.result.toAccountNumber && $scope.result.toAccountNumber.length < 9);*/
         $scope.statusToShowError = !$scope.newStatus;
         hasError = $scope.payDateShowError || $scope.payBankShowError || $scope.payBankRefShowError ||
             $scope.toBankShowError || $scope.toAccountNumberShowError || $scope.toAccountNumberShowMaxError || $scope.statusToShowError;
         return !hasError;
     }
     $scope.$watch('result', function(newVal, oldVal){
         if($scope.showError)
             validateForm();
     }, true);
     $scope.$watch('newStatus', function(newVal, oldVal){
         if($scope.showError)
             validateForm();
     }, true);

     $scope.processRequestRefundMoney = function () {
         $scope.showError = true;

         if ($scope.result.status == 1 || $scope.result.status == 2) {
             if(!validateForm()) {
                 toastr.error($translate.instant('common.completeFormCarefully'));
                 return;
             }
             $scope.cRequestRefundMoney = {
                 id: $scope.result.id,
                 status: $scope.newStatus,
                 payBankId: $scope.result.payBankId,
                 payBankRef: $scope.result.payBankRef,
                 payDate: $scope.result.payDate,
                 payDsc: $scope.result.payDesc,
                 toBankId: $scope.result.toBankId,
                 financeDestName: $scope.result.financeDestName,
                 financeDestValue: $scope.result.financeDestValue
             }
         }else{
             $scope.cRequestRefundMoney = {
                 id: $scope.result.id,
                 status: $scope.newStatus
             }
             if($scope.newStatus == 3) {
                 if($scope.result.payDesc == null || $scope.result.payDesc.length == 0) {
                     toastr.error("توضیحات نمی تواند خالی باشد.لطفاً دلیل رد را وارد کنید.");
                     return;
                 }
                 else
                     $scope.cRequestRefundMoney.payDsc = $scope.result.payDesc;
             }
         }
         NProgress.start();
         $.blockUI();

         httpServices.processRequestRefundMoney($scope.cRequestRefundMoney)
             .then(function (response) {
                 var data = response.data;
                 toastr.success($translate.instant('common.successfullySavedMessage'));
                 //httpServices.redirect(Emix.Pages.refundList);
                 $scope.showError = false;
                 sharedInfo.setNeedRefresh(true);
                 getRequestRefundMoneyInfo($scope.result.id);
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
             })
             .finally(function () {
                 NProgress.done();
                 $.unblockUI();
             });
     };

     $scope.closeModal = function(){
         $modalInstance.dismiss('cancel');
     }

 }]);
