
; (function () {
    "use strict";

    function setup($) {

        $.blockUI = function (opts) { install(window, opts); };
        $.unblockUI = function (opts) { remove(window, opts); };

        var pageBlock = null;

        function install(_window, _option) {

            if (pageBlock)
                remove();

            pageBlock = $('<div class="loader"><div class="spinner"></div></div>');

            var pNode = _option ? _option.parentNode ? _option.parentNode : "body" : "body";
            pageBlock.prependTo(pNode);
        }

        // remove the block
        function remove() {
            pageBlock && pageBlock.fadeOut("fast", function () {
                if (pageBlock !== null) {
                    pageBlock.remove();
                    pageBlock = null;
                }
            });
        }
    }

    /*global define:true */
    if (typeof define === 'function' && define.amd && define.amd.jQuery) {
        define(['jquery'], setup);
    } else {
        setup(jQuery);
    }
})();