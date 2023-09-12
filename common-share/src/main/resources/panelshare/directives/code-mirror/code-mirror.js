angular.module('emixApp.directives').directive('codeMirror', ['$timeout', function($timeout) {
    return {
        restrict: 'E',
        replace: true,
        templateUrl: '/panelshare/directives/code-mirror/code-mirror.html',
        scope: {
            /*container: '=',*/
            ngContent: '=',
            readonly: '=',
            theme: '@',
            lineNumbers: '@',
            syntax: '@'
        },
        link: function(scope, element, attrs) {
            var textarea = element.find('textarea')[0];
            var showLineNumbers = scope.lineNumbers === 'true' ? true : false;
            var showLineNumbers = scope.lineNumbers === 'true' ? true : false;
            var defaultTheme = 'tomorrow-night-eighties';
            scope.ngContent = scope.ngContent || '';
            scope.readonly = scope.readonly || false;
            var codeMirrorConfig = {
                direction: "ltr",
                lineNumbers: showLineNumbers,
                /*mode: scope.syntax || 'javascript',*/
                mode: scope.syntax || 'css',
                matchBrackets: false,
                theme: scope.theme || defaultTheme,
                value: scope.ngContent,
                lineWrapping: true,
                readOnly: scope.readonly,
                extraKeys: {
                    "F11": function(cm) {
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function(cm) {
                        if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
                    },
                    "Alt-F": "findPersistent",
                    "Ctrl-Space": "autocomplete"
                }
            };

            //scope.syntax = 'javascript';
            var myCodeMirror = CodeMirror.fromTextArea(textarea, codeMirrorConfig);

            myCodeMirror.setSize('100%')

            // If we have content coming from an ajax call or otherwise, asign it
            var unwatch = scope.$watch('ngContent', function(newValue, oldValue) {
            /*var unwatch = scope.$watch('content', function(oldValue, newValue) {*/
                newValue = newValue || '';
                if(newValue !== '') {
                    myCodeMirror.setValue(newValue);
                    unwatch();
                }
                else if(newValue === '' && oldValue === '') {
                    //unwatch();
                }
            });

            // Change the mode (syntax) according to dropdown
            scope.$watch('syntax', function(oldValue, newValue) {
                myCodeMirror.setOption('mode', scope.syntax);
            });

            // Set the codemirror value to the scope
            myCodeMirror.on('change', function(e) {
                $timeout(function() {
                    scope.ngContent = myCodeMirror.getValue();
                    /*scope.content = myCodeMirror.getValue();*/
                }, 300);
            });

        }
    };
}
]);