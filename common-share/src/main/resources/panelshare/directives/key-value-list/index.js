angular.module('emixApp.directives').directive('ngKeyValueList', ['sharedInfo', 'httpServices', '$rootScope', 'shareFunc', '$compile', '$timeout', '$translate',
    function (sharedInfo, httpServices, $rootScope, shareFunc, $compile, $timeout, $translate) {
    'use strict';

    return {
        scope: {
            options: '=',
            events: '='
        },
        templateUrl: '/panelshare/directives/key-value-list/template.html' + $rootScope.version,
        link: function (scope, element, attrs) {

            /*#region Init Options*/
            scope.options.keyValueList = scope.options.keyValueList || [];
            scope.options.keyName = scope.options.keyName || 'key';
            scope.options.valueName = scope.options.valueName || 'value';
            //scope.options.keyCaption = scope.options.keyCaption || '';
            //scope.options.valueCaption = scope.options.valueCaption || '';
            scope.options.blockClass = scope.options.blockClass || 'bzttag';
            scope.value = '';
            /*#endregion*/

            /*#region Utils*/
            var checkDuplicateAttribute = function (newItem, editMode) {
                editMode = editMode === true;
                var find = false;
                scope.options.keyValueList.forEach(function (item) {
                    if (!find && item[scope.options.keyName].replace(/\s/g, '') === newItem.replace(/\s/g, '')) {
                        if (!(editMode && item[scope.options.keyName] === scope.oldKeyValue[scope.options.keyName]))
                            find = true;
                    }
                });
                return find;
            }

            scope.checkKeyValue = function (editMode) {
                editMode = editMode === true;
                if (!scope.txtKey || !scope.txtValue) {
                    scope.showAttributeError = true;
                    if (!scope.txtKey)
                        angular.element('#txtKey').focus();
                    else if (!scope.txtValue)
                        angular.element('#txtValue').focus();
                    return false;
                }

                if (checkDuplicateAttribute(scope.txtKey, editMode)) {
                    toastr.error($translate.instant('shop.attributeIsDuplicate', {'attributeName': scope.txtKey}));
                    angular.element('#txtKey').focus();
                    return false;
                }
                return true;
            }
            scope.addKeyValue = function () {
                scope.showAttributeError = false;

                if (!scope.checkKeyValue())
                    return;
                var keyValue = {id: 0};
                keyValue[scope.options.keyName] = scope.txtKey;
                keyValue[scope.options.valueName] = scope.txtValue;

                scope.options.keyValueList.push(keyValue);

                scope.resetKeyValueForm();
            }
            scope.removeKeyValue = function (keyValue) {
                var itemPos = scope.options.keyValueList.indexOf(keyValue);
                if (itemPos > -1)
                    scope.options.keyValueList.splice(itemPos, 1);
            }
            scope.bindEditKeyValue = function (keyValue) {
                scope.oldKeyValue = keyValue;
                scope.txtKey = keyValue[scope.options.keyName];
                scope.txtValue = keyValue[scope.options.valueName];
                scope.keyValueEditMode = true;
            }
            scope.editKeyValue = function () {

                if (scope.oldKeyValue[scope.options.keyName] === scope.txtKey && scope.oldKeyValue[scope.options.valueName] === scope.txtValue) {
                    scope.cancelEditKeyValue();
                    return;
                }
                scope.showAttributeError = false;

                if (!scope.checkKeyValue(true))
                    return;

                var index = scope.options.keyValueList.indexOf(scope.oldKeyValue);
                scope.options.keyValueList[index][scope.options.keyName] = scope.txtKey;
                scope.options.keyValueList[index][scope.options.valueName] = scope.txtValue;

                scope.resetKeyValueForm();

            }
            scope.cancelEditKeyValue = function () {
                scope.keyValueEditMode = false;
                scope.resetKeyValueForm();
            }
            scope.resetKeyValueForm = function () {
                scope.txtKey = scope.txtValue = null;
                scope.showAttributeError = scope.keyValueEditMode = false;
                scope.oldKeyValue = null;
                angular.element('#txtKey').focus();
            }
            /*#endregion*/

            /*#region Events*/
            /*#endregion*/
        }
    }

}]);

