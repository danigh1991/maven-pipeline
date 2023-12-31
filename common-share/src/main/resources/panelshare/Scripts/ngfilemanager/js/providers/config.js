(function(angular) {
    'use strict';
    angular.module('FileManagerApp').provider('fileManagerConfig', function() {

        var values = {
            //appName: 'angular-filemanager v1.5',
            appName: 'app-filemanager',
            defaultLang: siteConfig.defaultLang,
            multiLang: true,

            //listUrl: 'bridges/php/handler.php',
            //uploadUrl: 'bridges/php/handler.php',
            //renameUrl: 'bridges/php/handler.php',
            //copyUrl: 'bridges/php/handler.php',
            //moveUrl: 'bridges/php/handler.php',
            //removeUrl: 'bridges/php/handler.php',
            //editUrl: 'bridges/php/handler.php',
            //getContentUrl: 'bridges/php/handler.php',
            //createFolderUrl: 'bridges/php/handler.php',
            //downloadFileUrl: 'bridges/php/handler.php',
            //downloadMultipleUrl: 'bridges/php/handler.php',
            //compressUrl: 'bridges/php/handler.php',
            //extractUrl: 'bridges/php/handler.php',
            //permissionsUrl: 'bridges/php/handler.php',
            listUrl: '/action/list',
            uploadUrl: '/action/upload',
            editImage: '/action/editImage',
            renameUrl: '/action/rename',
            copyUrl: '/action/copy',
            moveUrl: '/action/move',
            removeUrl: '/action/remove',
            editUrl: '/action/edit',
            getContentUrl: '/action/get-content',
            createFolderUrl: '/action/create-folder',
            downloadFileUrl: '/action/download',
            downloadMultipleUrl: '/action/download-multiple',
            compressUrl: '/action/compress',
            extractUrl: '/action/extract',
            permissionsUrl: '/action/permission',

            basePath: '/',
            imageBaseUrl: siteConfig.fileDomain,

            searchForm: true,
            sidebar: true,
            breadcrumb: true,
            allowedActions: {
                upload: true,
                editImage: true,
                rename: true,
                move: true,
                copy: true,
                edit: true,
                changePermissions: true,
                compress: true,
                compressChooseName: true,
                extract: true,
                download: true,
                downloadMultiple: true,
                preview: true,
                remove: true,
                createFolder: true,
                pickFiles: true,
                pickFolders: false
            },

            multipleDownloadFileName: 'angular-filemanager.zip',
            filterFileExtensions: [],
            showExtensionIcons: true,
            showSizeForDirectories: false,
            useBinarySizePrefixes: false,
            downloadFilesByAjax: true,
            previewImagesInModal: true,
            enablePermissionsRecursive: true,
            compressAsync: false,
            extractAsync: false,
            pickCallback: null,
            editCallback: null,

            isEditableFilePattern: /\.(txt|diff?|patch|svg|asc|cnf|cfg|conf|html?|.html|cfm|cgi|aspx?|ini|pl|py|md|css|cs|js|jsp|log|htaccess|htpasswd|gitignore|gitattributes|env|json|atom|eml|rss|markdown|sql|xml|xslt?|sh|rb|as|bat|cmd|cob|for|ftn|frm|frx|inc|lisp|scm|coffee|php[3-6]?|java|c|cbl|go|h|scala|vb|tmpl|lock|go|yml|yaml|tsv|lst)$/i,
            isImageFilePattern: /\.(jpe?g|gif|bmp|png|svg|tiff?)$/i,
            isExtractableFilePattern: /\.(gz|tar|rar|g?zip)$/i,
            tplPath: 'src/templates'
        };

        return {
            $get: function() {
                return values;
            },
            set: function (constants) {
                angular.extend(values, constants);
            }
        };

    });
})(angular);
