angular.module('emixApp.directives').directive("limitToMax", function() {
    return {
        link: function(scope, element, attributes) {
            element.on("keydown keyup", function(e) {
                if (Number(element.val()) > Number(attributes.max) &&
                      e.keyCode != 46 // delete
                      &&
                      e.keyCode != 8 // backspace
                    ) {
                    e.preventDefault();
                    e.stopPropagation();
                    //element.val(attributes.max);
                    element.val('');
                    /*if(element.ngChange)
                        evel(element.ngChange);*/
                    toastr.error('مقدار وارد شده صحیح نمی باشد.حداکثر مقدار قابل قبول ' + attributes.max + ' می باشد.');
                }
                else if (attributes.min && element.val().length > 0 && Number(element.val()) < Number(attributes.min) &&
                    e.keyCode != 46 // delete
                    &&
                    e.keyCode != 8 // backspace
                ) {
                    e.preventDefault();
                    e.stopPropagation();
                    //element.val(attributes.max);
                    element.val('');
                    /*if(element.ngChange)
                        evel(element.ngChange);*/
                    toastr.error('مقدار وارد شده صحیح نمی باشد.حداقل مقدار قابل قبول ' + attributes.min + ' می باشد.');
                }
            });
        }
    };
});

angular.module('emixApp.directives').directive("preventTypingGreater", function () {
    return {
        link: function(scope, element, attributes) {
            var oldVal = null;
            element.on("keydown keyup", function(e) {
                if (Number(element.val()) > Number(attributes.max) &&
                      e.keyCode != 46 // delete
                      &&
                      e.keyCode != 8 // backspace
                    ) {
                    e.preventDefault();
                    e.stopPropagation();
                    element.val(oldVal);
                } else {
                    oldVal = Number(element.val());
                }
            });
        }
    };
});

angular.module('emixApp.directives').directive('enterEvent', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            var keyCode = event.which || event.keyCode;
            if(keyCode === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.enterEvent);
                });

                event.preventDefault();
            }
        });
    };
});