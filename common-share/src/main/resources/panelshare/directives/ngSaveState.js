angular.module('emixApp.directives').directive('ngSaveState', ['sharedInfo', 'shareFunc', '$rootScope', function (sharedInfo, shareFunc,$rootScope) {
    return {
        require: 'ngModel',
        scope: {
            ngModel: '=',
            afterRestoreSavedState: '&'
        },
        link: function (scope, element, attr, ngModelCtrl) {
            var disableSaveState = attr.disableSaveState === null || attr.disableSaveState === undefined ? false : shareFunc.getBoolDefaultValue(attr.disableSaveState);
            if(disableSaveState) return;
            scope.validStorageName = attr.ngSaveState !== null && attr.ngSaveState !== undefined && attr.ngSaveState.length > 0;
            scope.storageName = $rootScope.currentLang + '_' + attr.ngSaveState;

            //#region Events
            var firstLoad = true;
            scope.$watch('ngModel', function (newval, oldVal) {
                if (firstLoad && scope.validStorageName) {
                    scope.restoreState();
                    firstLoad = false;
                }
                else if(scope.validStorageName)
                    scope.storeState();
            });
            //#region

            //#region state
            scope.storeState = function () {
                sharedInfo.setState(scope.storageName, scope.ngModel);
            }
            scope.restoreState = function () {
                var val = sharedInfo.getState(scope.storageName);
                if (val) {
                    //if(val.$$hashKey) delete val.$$hashKey;
                    scope.ngModel = val;//angular.merge({}, scope.ngModel, val);
                    scope.afterRestoreSavedState && scope.afterRestoreSavedState();
                }
            }
            //#region
        }
    };
}]);