(function (angular) {
    'use strict';
    angular.module('FileManagerApp').factory('item', ['fileManagerConfig', 'chmod', function (fileManagerConfig, Chmod) {

        var getRouteFileByBasePath = function (rootPath) {
            //return rootPath.substr(0, 1);
            rootPath = rootPath.toLowerCase();
            switch (rootPath) {
                case 'article':
                    return 'a';
                case 'product':
                    return 'k';//Kala
                case 'site':
                    return 'r';//Root
                case 'page':
                    return 'b';//Barge
                case 'linked_box':
                    return 'c';//content of linkedbox
                case 'slider_content':
                    return 'o';//Owlcarousel Slider Content
                case 'site_theme':
                    return 'g';//ghaleb
                case 'business_type':
                    return 'z';
                case 'store_branch':
                    return 'n';//store branch
                case 'pwa_config':
                    return 'y';//store branch
                default:
                    return 'unkown';
            }
        }
        var getFolderPath = function (pathArray) {
            var path = pathArray.slice(2);
            return path.length > 0 ? '-' + path.join('-') : '';
        }
        var Item = function (model, path) {
            var rawModel = {
                name: model && model.name || '',
                path: path || [],
                type: model && model.type || 'file',
                size: model && parseInt(model.size || 0),
                date: parseMySQLDate(model && model.date),
                //lastModifiedDate: model && model.date,
                perms: new Chmod(model && model.rights),
                content: model && model.content || '',
                recursive: false,
                fullPath: function () {
                    var path = this.path.filter(Boolean);
                    return ('/' + path.join('/') + '/' + this.name).replace(/\/\//, '/');
                },
                additionaFolderUrl: function(){
                    return getFolderPath(this.path ? this.path.filter(Boolean) : []);
                },
                //Mehdib
                thumbnailUrl: (model && model.name && model.name.split('.').length > 1 && (model.name.split('.')[1] == 'jpg' || model.name.split('.')[1] == 'jpeg' || model.name.split('.')[1] == 'png' || model.name.split('.')[1] == 'ico' || model.name.split('.')[1] == 'webp')) ? fileManagerConfig.imageBaseUrl + '/img/' + getRouteFileByBasePath(path[0]) + '/t/' + path[1] + getFolderPath(path.filter(Boolean)) + '-' + model.name.split('.')[0] + '/unknow.' + model.name.split('.')[1] + '?dt=' + parseMySQLDate(model.date).getTime() : '',
                //routeFile: (model && model.name && (model.name.split('.')[1] == 'jpg' || model.name.split('.')[1] == 'jpeg' || model.name.split('.')[1] == 'png' || model.name.split('.')[1] == 'ico' || model.name.split('.')[1] == 'webp')) ? getRouteFileByBasePath(path[0]) : '',
                routeFile: getRouteFileByBasePath(path && path.length > 0 ? path[0] : ''),
                extension: model && model.name && model.name.split('.').length > 1 ? model.name.split('.')[1] : ''
                //Mehdib
            };

            this.error = '';
            this.processing = false;

            this.model = angular.copy(rawModel);
            this.tempModel = angular.copy(rawModel);

            //MehdiB
            function parseMySQLDate(mysqlDate) {
                var datetime = new Date(mysqlDate);
                //var d = (datetime || '').toString().split(/[- :]/);
                //return new Date(d[0], d[1] - 1, d[2], d[3], d[4], d[5]);
                return datetime;
            }
        };

        Item.prototype.update = function () {
            angular.extend(this.model, angular.copy(this.tempModel));
        };

        Item.prototype.revert = function () {
            angular.extend(this.tempModel, angular.copy(this.model));
            this.error = '';
        };

        Item.prototype.isFolder = function () {
            return this.model.type === 'dir';
        };

        Item.prototype.isEditable = function () {
            return !this.isFolder() && fileManagerConfig.isEditableFilePattern.test(this.model.name);
        };

        Item.prototype.isImage = function () {
            return fileManagerConfig.isImageFilePattern.test(this.model.name);
        };

        Item.prototype.isCompressible = function () {
            return this.isFolder();
        };

        Item.prototype.isExtractable = function () {
            return !this.isFolder() && fileManagerConfig.isExtractableFilePattern.test(this.model.name);
        };

        Item.prototype.isSelectable = function () {
            return (this.isFolder() && fileManagerConfig.allowedActions.pickFolders) || (!this.isFolder() && fileManagerConfig.allowedActions.pickFiles);
        };

        return Item;
    }]);
})(angular);