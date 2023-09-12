angular.module('ui.emixLte', [])
    .directive('ngToggleAsideLte', function () {
        'use strict';

        return {
            restrict: 'A',
            link: function ($scope, $elem/*, $attr*/) {
                $($elem).click(function (e) {
                    e.preventDefault();

                    //Enable sidebar push menu
                    $("body").toggleClass('sidebar-collapse');
                    $("body").toggleClass('sidebar-open');

                    if (typeof $disowl != "undefined") {
                        $disowl.trigger('refresh.owl.carousel');
                    }

                });
            }
        };
    }).directive('ngMobileToggleAsideLte', function () {
        'use strict';

        return {
            restrict: 'A',
            link: function ($scope, $elem/*, $attr*/) {
                $($elem).click(function (e) {
                    if ($(window).width() <= 767 && $("body").hasClass("sidebar-open")) {
                        $("body").removeClass('sidebar-open');
                        $("body").addClass('sidebar-collapse');
                        /*$("body").toggleClass('sidebar-collapse');
                        $("body").toggleClass('sidebar-open');*/
                    }
                });
            }
        };
    }).directive('ngSidebarTree', function () {
        'use strict';

        return {
            restrict: 'A',
            link: function ($scope, $elem/*, $attr*/) {
                $(document).ready(function () {
                    $("li a", $($elem)).click(function (e) {
                        //Get the clicked link and the next element
                        var $this = $(this);
                        var checkElement = $this.next();

                        //Check if the next element is a menu and is visible
                        if ((checkElement.is('.treeview-menu')) && (checkElement.is(':visible'))) {
                            //Close the menu
                            checkElement.slideUp('normal', function () {
                                checkElement.removeClass('menu-open');
                            });
                            checkElement.parent("li").removeClass("active");
                        }
                            //If the menu is not visible
                        else if ((checkElement.is('.treeview-menu')) && (!checkElement.is(':visible'))) {
                            //Get the parent menu
                            var parent = $this.parents('ul').first();
                            //Close all open menus within the parent
                            var ul = parent.find('ul:visible').slideUp('normal');
                            //Remove the menu-open class from the parent
                            ul.removeClass('menu-open');
                            //Get the parent li
                            var parent_li = $this.parent("li");

                            //Open the target menu and add the menu-open class
                            checkElement.slideDown('normal', function () {
                                //Add the class active to the parent li
                                checkElement.addClass('menu-open');
                                parent.find('li.active').removeClass('active');
                                parent_li.addClass('active');
                            });
                        }
                        //if this isn't a link, prevent the page from being redirected
                        if (checkElement.is('.treeview-menu')) {
                            e.preventDefault();
                        }
                    });
                });
            }
        };
    }).directive('ngFixLayoutOnResize', ['$timeout', '$rootScope', function ($timeout, $rootScope) {
        'use strict';

        return {
            restrict: 'A',
            link: function ($scope, $elem/*, $attr*/) {
               
                var fnFixRightSize = function () {
                    //Get window height and the wrapper height
                    var neg = $('.main-header').outerHeight() + $('.main-footer').outerHeight();
                    var window_height = $(window).height();
                    var sidebar_height = $(".sidebar").height();
                    //Set the min-height of the content and sidebar based on the
                    //the height of the document.
                    if ($("body").hasClass("fixed")) {
                        $(".content-wrapper, .right-side").css('min-height', window_height - $('.main-footer').outerHeight());
                    } else {
                        if (window_height >= sidebar_height) {
                            $(".content-wrapper, .right-side").css('min-height', window_height - neg);
                        } else {
                            $(".content-wrapper, .right-side").css('min-height', sidebar_height);
                        }
                    }
                };
                var fnFixSidebar = function () {
                    //Make sure the body tag has the .fixed class
                    //if (!$("body").hasClass("fixed")) {
                    //    if (typeof $.fn.slimScroll != 'undefined') {
                    //        $(".sidebar").slimScroll({ destroy: true }).height("auto");
                    //    }
                    //    return;
                    //} else if (typeof $.fn.slimScroll == 'undefined' && console) {
                    //    console.error("Error: the fixed layout requires the slimscroll plugin!");
                    //}
                    //Enable slimscroll for fixed layout
                    //if ($.AdminLTE.options.sidebarSlimScroll) {
                    //if (true) {
                    //    if (typeof $.fn.slimScroll != 'undefined') {
                    //        //Distroy if it exists
                    //        $(".sidebar").slimScroll({ destroy: true }).height("auto");
                    //        //Add slimscroll
                    //        $(".sidebar").slimscroll({
                    //            height: ($(window).height() - $(".main-header").height() - 50) + "px",
                    //            color: "#fff",
                    //            size: "5px"
                    //        });
                    //    }
                    //}
                    //if ($('body').data('device') == 'mobile') {
                    //    ($(window).height() - $(".main-header").height() - 50) + "px";
                    //}
                    //else {

                    //}
                };

                //called when window is resized
                $(document).ready(function () {
                    fnFixRightSize();
                    fnFixSidebar();
                    $(window, ".wrapper").resize(function () {
                        fnFixRightSize();
                        fnFixSidebar();
                    });
                });

                //called when view is changed
                $rootScope.$on('$viewContentLoaded', function () {
                    fnFixRightSize();
                    fnFixSidebar();
                });

                $rootScope.$on("NeedResizePage", function () {
                    fnFixRightSize();
                    fnFixSidebar();
                });
            }
        };
    }]);