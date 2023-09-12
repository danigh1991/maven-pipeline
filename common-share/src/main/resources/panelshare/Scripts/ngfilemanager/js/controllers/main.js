(function (angular) {
    'use strict';
    angular.module('FileManagerApp').controller('FileManagerCtrl', [
        '$scope', '$rootScope', '$window', '$translate', 'fileManagerConfig', 'item', 'fileNavigator', 'apiMiddleware', '$timeout',
        function ($scope, $rootScope, $window, $translate, fileManagerConfig, Item, FileNavigator, ApiMiddleware, $timeout) {

            var $storage = $window.localStorage;
            $scope.config = fileManagerConfig;
            $scope.reverse = false;
            $scope.predicate = ['model.type', 'model.name'];
            $scope.order = function (predicate) {
                $scope.reverse = ($scope.predicate[1] === predicate) ? !$scope.reverse : false;
                $scope.predicate[1] = predicate;
            };
            $scope.query = '';
            $scope.fileNavigator = new FileNavigator();
            $scope.apiMiddleware = new ApiMiddleware();
            $scope.uploadFileList = [];
            $scope.viewTemplate = $storage.getItem('viewTemplate') || 'main-icons.html';
            $scope.fileList = [];
            $scope.temps = [];

            $scope.$watch('temps', function () {
                if ($scope.singleSelection()) {
                    $scope.temp = $scope.singleSelection();
                } else {
                    $scope.temp = new Item({ rights: 644 });
                    $scope.temp.multiple = true;
                }
                $scope.temp.revert();
            });

            $scope.fileNavigator.onRefresh = function () {
                $scope.temps = [];
                $scope.query = '';
                $rootScope.selectedModalPath = $scope.fileNavigator.currentPath;
            };

            $scope.setTemplate = function (name) {
                $storage.setItem('viewTemplate', name);
                $scope.viewTemplate = name;
            };

            $scope.changeLanguage = function (locale) {
                if (locale) {
                    /*$storage.setItem('language', locale);
                    return $translate.use(locale);*/
                }
                //$translate.use($storage.getItem('language') || fileManagerConfig.defaultLang);
            };

            $scope.isSelected = function (item) {
                return $scope.temps.indexOf(item) !== -1;
            };

            $scope.selectOrUnselect = function (item, $event) {
                var indexInTemp = $scope.temps.indexOf(item);
                var isRightClick = $event && $event.which == 3;

                if ($event && $event.target.hasAttribute('prevent')) {
                    $scope.temps = [];
                    return;
                }
                if (!item || (isRightClick && $scope.isSelected(item))) {
                    return;
                }
                if ($event && $event.shiftKey && !isRightClick) {
                    var list = $scope.fileList;
                    var indexInList = list.indexOf(item);
                    var lastSelected = $scope.temps[0];
                    var i = list.indexOf(lastSelected);
                    var current = undefined;
                    if (lastSelected && list.indexOf(lastSelected) < indexInList) {
                        $scope.temps = [];
                        while (i <= indexInList) {
                            current = list[i];
                            !$scope.isSelected(current) && $scope.temps.push(current);
                            i++;
                        }
                        return;
                    }
                    if (lastSelected && list.indexOf(lastSelected) > indexInList) {
                        $scope.temps = [];
                        while (i >= indexInList) {
                            current = list[i];
                            !$scope.isSelected(current) && $scope.temps.push(current);
                            i--;
                        }
                        return;
                    }
                }
                if ($event && !isRightClick && ($event.ctrlKey || $event.metaKey)) {
                    $scope.isSelected(item) ? $scope.temps.splice(indexInTemp, 1) : $scope.temps.push(item);
                    return;
                }
                $scope.temps = [item];
            };

            $scope.singleSelection = function () {
                return $scope.temps.length === 1 && $scope.temps[0];
            };

            $scope.totalSelecteds = function () {
                return {
                    total: $scope.temps.length
                };
            };

            $scope.selectionHas = function (type) {
                return $scope.temps.find(function (item) {
                    return item && item.model.type === type;
                });
            };

            $scope.prepareNewFolder = function () {
                var item = new Item(null, $scope.fileNavigator.currentPath);
                $scope.temps = [item];
                return item;
            };

            //Mehdib
            $scope.mrootScope = $rootScope;
            //Mehdib
            $scope.smartClick = function (item) {
                var pick = $scope.config.allowedActions.pickFiles;
                if (item.isFolder()) {
                    return $scope.fileNavigator.folderClick(item);
                }

                if (typeof $scope.config.pickCallback === 'function' && pick) {
                    //Mehdib
                    var callbackSuccess = $scope.config.pickCallback($rootScope, item.model);
                    //Mehdib
                    if (callbackSuccess === true) {
                        return;
                    }
                }

                if (item.isImage()) {
                    if ($scope.config.previewImagesInModal) {
                        return $scope.openImagePreview(item);
                    }
                    return $scope.apiMiddleware.download(item, true);
                }

                if (item.isEditable()) {
                    return $scope.openEditItem(item);
                }
            };

            $scope.openImagePreview = function () {
                var item = $scope.singleSelection();
                $scope.apiMiddleware.apiHandler.inprocess = true;
                $scope.modal('imagepreview', null, true)
                    .find('#imagepreview-target')
                    .attr('src', $scope.getUrl(item))
                    .unbind('load error')
                    .on('load error', function () {
                        $scope.apiMiddleware.apiHandler.inprocess = false;
                        $scope.$apply();
                    });
            };

            //MehdiB Upload Image From Url
            var bindEvent = function(){
                var newimagepreviewtarget = document.getElementById('newimagepreview-target');
                //newimagepreviewtarget.crossOrigin = 'Anonymous';
                newimagepreviewtarget.onload = function() {

                    if (this.src.startsWith('data:image'))
                        return;

                    // Create an html canvas element
                    var canvas = document.createElement('CANVAS');
                    // Create a 2d context
                    var ctx = canvas.getContext('2d');
                    var dataURL;
                    // Resize the canavas to the original image dimensions
                    canvas.height = this.naturalHeight;
                    canvas.width = this.naturalWidth;
                    // Draw the image to a canvas
                    ctx.drawImage(this, 0, 0);
                    // Convert the canvas to a data url
                    dataURL = canvas.toDataURL('image/jpg');
                    // Return the data url via callback
                    this.src = dataURL
                    // Mark the canvas to be ready for garbage
                    // collection
                    canvas = null;
                };
                newimagepreviewtarget.onerror = function(){
                    var err = "بازیابی تصویر برای ادرس ارسالی با خطا مواجه شد.";//response;
                    toastr.error(err);
                };
            }
            $scope.uploadFileUrlChange = function (url) {
                $('#newimagepreview-target').attr('src', url);
                /*bindEvent();
                var newimagepreviewtarget = document.getElementById('newimagepreview-target');
                newimagepreviewtarget.src = url;
                // make sure the load event fires for cached images too
                if (newimagepreviewtarget.complete || newimagepreviewtarget.complete === undefined) {
                    // Flush cache
                    newimagepreviewtarget.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';
                    // Try again
                    newimagepreviewtarget.src = url;
                }*/
            }
            $scope.newimageEditorOptios = { aspectRatio: sessionStorage.getItem('fmanager-crop-ratio'), afterEditImageFunc: 'afterImageEditFromUrl' };
            $scope.afterImageEditFromUrl = function () {
                var item = $scope.singleSelection();
                $scope.enableCrop = true;
                $scope.newimageEditorOptios.applyImage();
                $timeout(function () {
                    $('#newimagepreview-target').attr('src', $scope.newimageEditorOptios.imageAfterEdit);
                });
                $scope.uploadFileList = [{ name: 'file1.jpg', type: 'image/jpg', croppedImage: $scope.newimageEditorOptios.imageAfterEdit }];
                //Mehdib
                $scope.onBeforeUploadItem();
                //Mehdib

                $scope.apiMiddleware.upload($scope.uploadFileList, $scope.fileNavigator.currentPath).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.uploadFileList = [];
                    $scope.newimageEditorOptios.imageAfterEdit = null;
                    var uploadfilefromurlModal = $scope.modal('uploadfilefromurl', true, true);
                    uploadfilefromurlModal.find('#txtImageUrlForUpload').val('');
                    uploadfilefromurlModal.find('#newimagepreview-target').attr('src','');
                }, function (data) {
                    var errorMsg = data.result && data.result.error || $translate.instant('error_uploading_files');
                    $scope.apiMiddleware.apiHandler.error = errorMsg;
                });
                //alert($scope.newimageEditorOptios.imageAfterEdit);
            }

            //MehdiB
            var dbClickFlag = true;
            $scope.editImage = function () {
                var item = $scope.singleSelection();
                $scope.apiMiddleware.apiHandler.inprocess = true;
                $scope.getBase64(item).then(function (base64) {
                    var editModal = $scope.modal('editimage', null, true)
                    editModal.find('#editimagepreview-target')
                        .attr('src', base64)
                        .unbind('load error')
                        .on('load error', function () {
                            //$scope.imageToBase64(this)                                      
                            $scope.apiMiddleware.apiHandler.inprocess = false;
                            $scope.$apply(function () {
                                //$timeout(function () {
                                //if (dbClickFlag) {
                                //    $('#editimagepreview-target').dblclick();
                                //    dbClickFlag = false;
                                //}
                                //});
                            });
                        });
                }, function () {
                    $scope.apiMiddleware.apiHandler.inprocess = false;
                    $scope.$apply();
                });
            };

            $scope.afterImageEdit = function () {
                var item = $scope.singleSelection();
                $scope.enableCrop = true;
                $scope.imageEditorOptios.applyImage();
                $timeout(function () {
                    $('#editimagepreview-target').attr('src', $scope.imageEditorOptios.imageAfterEdit);
                });
                /*$scope.uploadFileList = [{ name: item.model.name, type: 'image/jpg', croppedImage: $scope.imageEditorOptios.imageAfterEdit }];
                //Mehdib
                $scope.onBeforeUploadItem();
                //Mehdib

                $scope.apiMiddleware.editImage($scope.uploadFileList, $scope.fileNavigator.currentPath).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.uploadFileList = [];
                    $scope.imageEditorOptios.imageAfterEdit = null;
                    $scope.modal('editimage', true);
                }, function (data) {
                    var errorMsg = data.result && data.result.error || $translate.instant('error_uploading_files');
                    $scope.apiMiddleware.apiHandler.error = errorMsg;
                });*/
                //alert($scope.imageEditorOptios.imageAfterEdit);
            }
            //Mehdib
            $scope.saveAfterImageEdit = function () {
                var item = $scope.singleSelection();
                $scope.uploadFileList = [{ name: item.model.name, type: 'image/jpg', croppedImage: $scope.imageEditorOptios.imageAfterEdit }];
                //Mehdib
                $scope.onBeforeUploadItem();
                //Mehdib
                $scope.apiMiddleware.apiHandler.inprocess = true;
                $scope.apiMiddleware.editImage($scope.uploadFileList, $scope.fileNavigator.currentPath).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.uploadFileList = [];
                    $scope.imageEditorOptios.imageAfterEdit = null;
                    $scope.modal('editimage', true);
                    $scope.apiMiddleware.apiHandler.inprocess = false;
                }, function (data) {
                    var errorMsg = data.result && data.result.error || $translate.instant('error_uploading_files');
                    $scope.apiMiddleware.apiHandler.error = errorMsg;
                    $scope.apiMiddleware.apiHandler.inprocess = false;
                });
                //alert($scope.imageEditorOptios.imageAfterEdit);
            }
            //Mehdib
            //$scope.imageAfterEdit = '';
            $scope.imageEditorOptios = { aspectRatio: sessionStorage.getItem('fmanager-crop-ratio') };

            $scope.imageToBase64 = function (img, event) {

                //var canvas = document.createElement("canvas");
                //canvas.width = this.width;
                //canvas.height = this.height;

                //var ctx = canvas.getContext("2d");
                //ctx.drawImage(img, 0, 0);

                //var dataURL = canvas.toDataURL("image/png");
                //var base64Url = dataURL.replace(/^data:image\/(png|jpg);base64,/, "");

                //$('#editimagepreview-target')[0].src = base64Url;
                //alert('image could not be loaded');


            };
            //#region Editor Old Try Now Is a Directive
            //var show_animation = function () {
            //    jQuery('#loading_container').css('display', 'block');
            //    jQuery('#loading').css('opacity', '.7');
            //}

            //var hide_animation = function () {
            //    jQuery('#loading_container').fadeOut();
            //}
            //var ext_img = new Array('jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg', 'ico');
            //var image_editor = true;
            //if (image_editor) {
            //    var featherEditor = new Aviary.Feather({
            //        apiKey: "2444282ef4344e3dacdedc7a78f8877d", language: "en", theme: "light", tools: "all", maxSize: "1400",
            //        onReady: function () {
            //            hide_animation();
            //        },
            //        onSave: function (imageID, newURL) {
            //            show_animation();
            //            var img = document.getElementById(imageID);
            //            img.src = newURL;
            //            featherEditor.close();
            //            /*$.ajax({
         	//				type: "POST",
         	//				url: "ajax_calls.php?action=save_img",
         	//				data: { url: newURL, path:$('#sub_folder').val()+$('#fldr_value').val(), name:$('#aviary_img').attr('data-name') }
         	//			}).done(function( msg ) {
         	//				featherEditor.close();
         	//				d = new Date();
         	//				$("figure[data-name='"+$('#aviary_img').attr('data-name')+"']").find('img').each(function(){
         	//				$(this).attr('src',$(this).attr('src')+"?"+d.getTime());
         	//				});
         	//				$("figure[data-name='"+$('#aviary_img').attr('data-name')+"']").find('figcaption a.preview').each(function(){
         	//				$(this).attr('data-url',$(this).data('url')+"?"+d.getTime());
         	//				});
         	//				hide_animation();
         	//			});*/
            //            return false;
            //        },
            //        onError: function (errorObj) {
            //            //bootbox.alert(errorObj.message);
            //            hide_animation();
            //        }
            //    });
            //}
            //$scope.lunchEditor = function () {
            //    if (outputdata == null) {
            //        featherEditor.launch({
            //            image: "editimagepreview-target",
            //            url: document.getElementById("editimagepreview-target").getAttribute("src")
            //        });
            //        $('#avpw_save_button').hide();
            //        $("#avpw_save_button").after('<b class="avpw_button avpw_primary_button" id="avpwsubmit" onClick="getData()">Save</b>');
            //    } else {
            //        $('#avpw_fullscreen_bg').show();
            //        $('#avpw_controls').show();
            //    }
            //    return false;
            //};            
            //var outputdata = null;
            //function getData() {
            //    outputdata = $('#avpw_canvas_element')[0].toDataURL("image/jpeg");
            //    $('#imagres').attr('src', outputdata);
            //    $('#avpw_fullscreen_bg').hide();
            //    $('#avpw_controls').hide();
            //}
            //#endregion
            
            //MehdiB
            $scope.openEditItem = function () {
                var item = $scope.singleSelection();
                $scope.apiMiddleware.getContent(item).then(function (data) {
                    item.tempModel.content = item.model.content = data.result;
                });
                $scope.modal('edit');
            };

            $scope.modal = function (id, hide, returnElement) {
                var element = angular.element('#' + id);
                element.modal(hide ? 'hide' : 'show');
                $scope.apiMiddleware.apiHandler.error = '';
                $scope.apiMiddleware.apiHandler.asyncSuccess = false;
                return returnElement ? element : true;
            };

            $scope.modalWithPathSelector = function (id) {
                $rootScope.selectedModalPath = $scope.fileNavigator.currentPath;
                return $scope.modal(id);
            };

            $scope.isInThisPath = function (path) {
                var currentPath = $scope.fileNavigator.currentPath.join('/') + '/';
                return currentPath.indexOf(path + '/') !== -1;
            };

            $scope.edit = function () {
                $scope.apiMiddleware.edit($scope.singleSelection()).then(function () {
                    $scope.modal('edit', true);
                });
            };

            $scope.changePermissions = function () {
                $scope.apiMiddleware.changePermissions($scope.temps, $scope.temp).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.modal('changepermissions', true);
                });
            };

            $scope.download = function () {
                var item = $scope.singleSelection();
                if ($scope.selectionHas('dir')) {
                    return;
                }
                if (item) {
                    return $scope.apiMiddleware.download(item);
                }
                return $scope.apiMiddleware.downloadMultiple($scope.temps);
            };

            $scope.copy = function () {
                var item = $scope.singleSelection();
                if (item) {
                    var name = item.tempModel.name.trim();
                    var nameExists = $scope.fileNavigator.fileNameExists(name);
                    if (nameExists && validateSamePath(item)) {
                        $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                        return false;
                    }
                    if (!name) {
                        $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                        return false;
                    }
                }
                $scope.apiMiddleware.copy($scope.temps, $rootScope.selectedModalPath).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.modal('copy', true);
                });
            };

            $scope.compress = function () {
                var name = $scope.temp.tempModel.name.trim();
                var nameExists = $scope.fileNavigator.fileNameExists(name);

                if (nameExists && validateSamePath($scope.temp)) {
                    $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                    return false;
                }
                if (!name) {
                    $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                    return false;
                }

                $scope.apiMiddleware.compress($scope.temps, name, $rootScope.selectedModalPath).then(function () {
                    $scope.fileNavigator.refresh();
                    if (!$scope.config.compressAsync) {
                        return $scope.modal('compress', true);
                    }
                    $scope.apiMiddleware.apiHandler.asyncSuccess = true;
                }, function () {
                    $scope.apiMiddleware.apiHandler.asyncSuccess = false;
                });
            };

            $scope.extract = function () {
                var item = $scope.temp;
                var name = $scope.temp.tempModel.name.trim();
                var nameExists = $scope.fileNavigator.fileNameExists(name);

                if (nameExists && validateSamePath($scope.temp)) {
                    $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                    return false;
                }
                if (!name) {
                    $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                    return false;
                }

                $scope.apiMiddleware.extract(item, name, $rootScope.selectedModalPath).then(function () {
                    $scope.fileNavigator.refresh();
                    if (!$scope.config.extractAsync) {
                        return $scope.modal('extract', true);
                    }
                    $scope.apiMiddleware.apiHandler.asyncSuccess = true;
                }, function () {
                    $scope.apiMiddleware.apiHandler.asyncSuccess = false;
                });
            };

            $scope.remove = function () {
                $scope.apiMiddleware.remove($scope.temps).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.modal('remove', true);
                });
            };

            $scope.move = function () {
                var anyItem = $scope.singleSelection() || $scope.temps[0];
                if (anyItem && validateSamePath(anyItem)) {
                    $scope.apiMiddleware.apiHandler.error = $translate.instant('error_cannot_move_same_path');
                    return false;
                }
                $scope.apiMiddleware.move($scope.temps, $rootScope.selectedModalPath).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.modal('move', true);
                });
            };

            $scope.rename = function () {
                var item = $scope.singleSelection();
                var name = item.tempModel.name;
                var samePath = item.tempModel.path.join('') === item.model.path.join('');
                if (!name || (samePath && $scope.fileNavigator.fileNameExists(name))) {
                    $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                    return false;
                }
                $scope.apiMiddleware.rename(item).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.modal('rename', true);
                });
            };

            $scope.createFolder = function () {
                var item = $scope.singleSelection();
                var name = item.tempModel.name;
                if (!name || $scope.fileNavigator.fileNameExists(name)) {
                    return $scope.apiMiddleware.apiHandler.error = $translate.instant('error_invalid_filename');
                }
                $scope.apiMiddleware.createFolder(item).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.modal('newfolder', true);
                });
            };

            $scope.addForUpload = function ($files) {
                //MehdiB
                if ($scope.enableCrop) {
                    angular.forEach($files, function (value, key) {
                        value.croppedImage = '';
                        var reader = new FileReader();
                        reader.onload = function (event) {
                            $scope.$apply(function () {
                                value.image = event.target.result;
                                $scope.uploadFileList = $scope.uploadFileList.concat(value);
                            });
                        };
                        reader.readAsDataURL(value);
                    });
                }
                else
                    $scope.uploadFileList = $scope.uploadFileList.concat($files);
                //MehdiB

                $scope.modal('uploadfile');
            };

            $scope.removeFromUpload = function (index) {
                $scope.uploadFileList.splice(index, 1);
            };

            //#region Mehdib
            var dataURItoBlob = function (dataURI) {
                var binary = atob(dataURI.split(',')[1]);
                var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
                var array = [];
                for (var i = 0; i < binary.length; i++) {
                    array.push(binary.charCodeAt(i));
                }
                return new Blob([new Uint8Array(array)], { type: mimeString });
            };
            function castToFile(file, blob) {
                //A Blob() is almost a File() - it's just missing the two properties below which we will add
                var retFile = new File([blob], file.name, { type: file.type, lastModified: file.lastModifiedDate })
                return retFile;
            }
            $scope.onBeforeUploadItem = function () {
                if ($scope.enableCrop) {
                    angular.forEach($scope.uploadFileList, function (value, key) {
                        var blob = dataURItoBlob(value.croppedImage);
                        $scope.uploadFileList[key] = castToFile($scope.uploadFileList[key], blob);
                    });
                }
            };

            $scope.dbClickOnImage = function (imageId) {
                $('#' + imageId).dblclick();
            }
            //#endregion Mehdib

            $scope.uploadFiles = function () {
                //Mehdib
                $scope.onBeforeUploadItem();
                //Mehdib

                $scope.apiMiddleware.upload($scope.uploadFileList, $scope.fileNavigator.currentPath).then(function () {
                    $scope.fileNavigator.refresh();
                    $scope.uploadFileList = [];
                    $scope.modal('uploadfile', true);
                }, function (data) {
                    var errorMsg = data.result && data.result.error || $translate.instant('error_uploading_files');
                    $scope.apiMiddleware.apiHandler.error = errorMsg;
                });
            };

            //#region ngImageCrop MehdiB
            var fmanagerEnableCrop = sessionStorage.getItem('fmanager-enable-crop') == 'true';
            $scope.enableCrop = fmanagerEnableCrop;
            $scope.changeOnFly = false;
            $scope.maximizeCrop = true;
            $scope.size = 'large';
            $scope.type = 'square';//'circle';
            $scope.aspectRatio = '5x4';
            //$scope.image = {
            //    originalInfo: {},
            //    cropInfo: {}
            //};
            $scope.imageDataURI = '';
            $scope.resImageDataURI = '';
            $scope.resImgFormat = 'image/jpeg';//png,webp
            $scope.resImgQuality = 1;//0..1
            $scope.selMinSize = 500;
            $scope.resImgSize = 1000;

            $scope.onChange = function ($dataURI) {
               // console.log('onChange fired');
            };
            $scope.onLoadBegin = function () {
               // console.log('onLoadBegin fired');
            };
            $scope.onLoadDone = function () {
               // console.log('onLoadDone fired');
            };
            $scope.onLoadError = function () {
               // console.log('onLoadError fired');
            };
            //#endregion ngImageCrop

            $scope.getUrl = function (_item) {
                return $scope.apiMiddleware.getUrl(_item);
            };

            //MehdiB
            $scope.getBase64 = function (_item) {
                return $scope.apiMiddleware.getBase64(_item);
            };
            //MehdiB

            var validateSamePath = function (item) {
                var selectedPath = $rootScope.selectedModalPath.join('');
                var selectedItemsPath = item && item.model.path.join('');
                return selectedItemsPath === selectedPath;
            };

            var getQueryParam = function (param) {
                var found = $window.location.search.substr(1).split('&').filter(function (item) {
                    return param === item.split('=')[0];
                });
                return found[0] && found[0].split('=')[1] || undefined;
            };

            $scope.changeLanguage(getQueryParam('lang'));
            $scope.isWindows = getQueryParam('server') === 'Windows';
            $scope.fileNavigator.refresh();

        }]);
})(angular);
