angular.module('emixApp.directives').directive("autofocus", function () {
    'use strict';
    return {
        restrict: 'A',
        link: function ($scope, elem, attrs) {
            var focusables = $("input[autofocus],textarea[autofocus],button,select");
            elem.bind('keydown', function (event) {
                var code = event.keyCode || event.which;
                if (code === 13) {
                    var current = focusables.index(this);
                    var next = focusables.eq(current + 1).length ? focusables.eq(current + 1) : focusables.eq(0);
                    next.focus();
                }
            });
        }
    };
});



function todaydate(type) {
    var week = new Array("يكشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنج شنبه", "جمعه", "شنبه")
    var months = new Array("فروردين", "ارديبهشت", "خرداد", "تير", "مرداد", "شهريور", "مهر", "آبان", "آذر", "دي", "بهمن", "اسفند");


    var a = new Date();
    var d = a.getDay();

    var day = a.getDate() + 1;
    var month = a.getMonth() + 1;
    var year = a.getYear();
    year = (year == 0) ? 2000 : year;
    (year < 1000) ? (year += 1900) : true;
    year -= ((month < 3) || ((month == 3) && (day < 21))) ? 622 : 621;

    switch (month) {
        case 1: (day < 21) ? (month = 10, day += 10) : (month = 11, day -= 20); break;
        case 2: (day < 20) ? (month = 11, day += 11) : (month = 12, day -= 19); break;
        case 3: (day < 21) ? (month = 12, day += 9) : (month = 1, day -= 20); break;
        case 4: (day < 21) ? (month = 1, day += 11) : (month = 2, day -= 20); break;
        case 5:
        case 6: (day < 22) ? (month -= 3, day += 10) : (month -= 2, day -= 21); break;
        case 7:
        case 8:
        case 9: (day < 23) ? (month -= 3, day += 9) : (month -= 2, day -= 22); break;
        case 10: (day < 23) ? (month = 7, day += 8) : (month = 8, day -= 22); break;
        case 11:
        case 12: (day < 22) ? (month -= 3, day += 9) : (month -= 2, day -= 21); break;
        default: break;
    }


    var ptoday = '';

    if (type == 'longdate')
        ptoday = week[d] + '  ' + day + '  ' + months[month - 1] + '  ' + year;
    else
        ptoday = year + "/" + month + "/" + day;


    return ptoday;
}
