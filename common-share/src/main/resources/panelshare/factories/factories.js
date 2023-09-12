String.prototype.toPersianDigits = function () {
    var id = ['۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'];
    return this.replace(/[0-9]/g, function (w) {
        return id[+w]
    });
};
String.prototype.toEnglishDigits = function () {
    var id = { '۰': '0', '۱': '1', '۲': '2', '۳': '3', '۴': '4', '۵': '5', '۶': '6', '۷': '7', '۸': '8', '۹': '9' };
    return this.replace(/[^0-9.]/g, function (w) {
        return id[w] || w;
    });
};

String.prototype.slugify = function () {
    var a = 'àáäâãåèéëêìíïîòóöôùúüûñçßÿœæŕśńṕẃǵǹḿǘẍźḧ·/_,:;';
    var b = 'aaaaaaeeeeiiiioooouuuuncsyoarsnpwgnmuxzh------';
    var p = new RegExp(a.split('').join('|'), 'g');
    return this.toString().toLowerCase().replaceAll('|', '')
        .replace(/\s+/g, '-') // Replace spaces with
        .replace(p, c => b.charAt(a.indexOf(c))) // Replace special characters
        .
        replace(/&/g, '-and-') // Replace & with ‘and’
        //.replace(/[^\w\-]+/g, '') // Remove all non-word characters
        .replace(/\-\-+/g, '-') // Replace multiple — with single -
        .replace(/^-+/, ''); // Trim — from start of text .replace(/-+$/, '') // Trim — from end of text
}

angular.module('emixApp.filters').filter('htmlToPlaintext', function() {
        return function(text) {
            //return  text ? String(text).replace(/<[^>]+>/gm, '').replace(/&nbsp;/gi, '') : '';
            return  text ? String(text).replace(/<("[^"]*"|'[^']*'|[^'">])*>/gi, '').replace(/^\s+|\s+$/g, '').replace(/&nbsp;/gi, '') : '';
        };
    }
);
angular.module('emixApp.filters').filter('reverse', function() {
    return function(items) {
        return items.slice().reverse();
    };
});

angular.module('emixApp.factories').factory('sharedInfo', ['$translate', 'httpServices', '$rootScope', function ($translate, httpServices, $rootScope) {

    var convertNumbers2English = function (string) {
        return string.replace(/[\u0660-\u0669\u06f0-\u06f9]/g, function (c) {
            return c.charCodeAt(0) & 0xf;
        });
    };

    var orderobject = {
        servicetype: 1,
        Size: 1,
        type: 1,
        images: []
    };

    var userData;

    setInStorage= function (mkey, mvalue, inSessionStorage) {
        inSessionStorage = inSessionStorage !== false;

        if (inSessionStorage) {
            sessionStorage.setItem(mkey, mvalue);
        }
        else {
            localStorage.setItem(mkey, mvalue);
        }
    }
    getFromStorage = function (mkey, inSessionStorage) {
        inSessionStorage = inSessionStorage !== false;

        if (inSessionStorage) {
            return sessionStorage.getItem(mkey);
        }
        else {
            return localStorage.getItem(mkey);
        }
    }

    return {
        dataType : {
            int: 'int',
            string: 'string',
            list: 'list',
            date: 'date',
            money: 'money',
            bool: 'bool'
        },
        strToJson: function (str) {
            var json;
            try {
                json = JSON.parse(str);
            } catch (e) {
                return null;
            }
            return json;
        },
        setState: function (key, value, inSessionStorage) {
            inSessionStorage = inSessionStorage !== false;
            setInStorage(key, JSON.stringify(value), inSessionStorage);
        },
        getState: function (key, inSessionStorage) {
            inSessionStorage = inSessionStorage !== false;
            var val = getFromStorage(key, inSessionStorage);
            var ret = this.strToJson(val);
            return ret == null ? val : ret;
        },
        setLocaleLang: function(lang){
            sessionStorage.setItem(siteConfig.langCookieName, lang);
        },
        getLocaleLang: function(lang){
            sessionStorage.getItem(siteConfig.langCookieName);
        },
        setCurrentArticleCat: function (value) {
            setInStorage('currentArticleCat', JSON.stringify(value));
        },
        getCurrentArticleCat: function () {
            var ret = getFromStorage('currentArticleCat');
            return JSON.parse(ret);
        },
        getValidDate: function(date){
            if(date==null)
                return date;
            var englishDate = date.toEnglishDigits();
            var enDateArr = englishDate.split('/');
            $.each(enDateArr, function (index, value) {
                enDateArr[index] = value.length > 1 ? value : '0' + value;
            });
            return enDateArr.join("/");
        },
        getValidTime: function (date) {
            return date.toEnglishDigits();
        },
        setOrder: function (selected) {
            orderobject.servicetype = selected;
        },
        saveUserData: function (muserData) {
            localStorage.setItem('userData', muserData);
        },
        getUserData: function () {
            return localStorage.getItem('userData');
        },
        saveUserId: function (muserId) {
            localStorage.setItem('userId', muserId);
        },
        getUserId: function () {
            return localStorage.getItem('userId') || 0;
        },
        saveUserLogoPath: function (userLogoPath) {
            localStorage.setItem('userLogoPath', userLogoPath);
        },
        getUserLogoPath: function () {
            return localStorage.getItem('userLogoPath') || '/u/0?v=1';
        },
        setCurrentStore: function (currentStoreId, currentStoreName) {
            localStorage.setItem('currentstoreid', currentStoreId);
            localStorage.setItem('currentstorename', currentStoreName);
        },
        getCurrentStore: function () {
            var id = localStorage.getItem('currentstoreid');
            var name = localStorage.getItem('currentstorename')
            return { id, name};
        },
        setCurrentBranch: function (currentBranchId,currentBranchName) {
            localStorage.setItem('currentbranchid', currentBranchId);
            localStorage.setItem('currentbranchname', currentBranchName);
        },
        getCurrentBranch: function () {
            var id = localStorage.getItem('currentbranchid');
            var name = localStorage.getItem('currentbranchname')
            return { id, name};
        },
        setCurrentDiscount: function (currentDiscountId, currentDiscountName) {
            localStorage.setItem('currentdiscountid', currentDiscountId);
            localStorage.setItem('currentdiscountname', currentDiscountName);
        },
        getCurrentDiscount: function () {
            var id = localStorage.getItem('currentdiscountid');
            var name = localStorage.getItem('currentdiscountname')
            return { id, name};
        },
        setCurrentProduct: function (product) {
            localStorage.setItem('currentProduct',  JSON.stringify(product));
        },
        getCurrentProduct: function () {
            var currentProduct = localStorage.getItem('currentProduct');
            return JSON.parse(currentProduct);
        },
        getCookieFromDocument: function (cookieKey) {
            if (!cookieKey) {
                return null;
            }
            return decodeURIComponent(document.cookie
					.replace(new RegExp("(?:(?:^|.*;)\\s*" + encodeURIComponent(cookieKey)
						.replace(/[\-\.\+\*]/g, "\\$&") + "\\s*\\=\\s*([^;]*).*$)|^.*$"), "$1")) || null;
        },
        getDefaultPosLat: function(){
            return 35.689198;
        },
        getDefaultPosLan: function () {
            return 51.388973;
        },
        getDefaultMapZoom: function () {
            return 15;
        },
        getDefaultMaxLinkCount: function () {
            return 15;
        },
        getFileManagerUrl: function (fileItem, id, title, completeUrl) {
            completeUrl = completeUrl || false;
            var cleanTitle = title.slugify();
            var cleanFileItem = fileItem.name.split('.')[0].replace(/\s+/g, '-').toLowerCase();//.replace('.jpg', '').replace('.jpeg', '');
            return (completeUrl ? imageBaseUrl : '') + '/img/'+fileItem.routeFile+'/' + id + fileItem.additionaFolderUrl() + '-' + cleanFileItem + '/' + cleanTitle + '.' + fileItem.extension;
        },
        getAricleFileUrl: function (fileItem, articleId, articleTitle) {
            var cleanArticleTitle = articleTitle.slugify();
            var cleanFileItem = fileItem.name.replace(/\s+/g, '-').toLowerCase().replace('.jpg', '').replace('.jpeg', '');
            return imageBaseUrl + '/img/a/' + articleId + '-' + cleanFileItem + '/' + cleanArticleTitle + '.jpg';
        },
        setFileManagerImageType: function (value) {
            sessionStorage.setItem('fmanager-img-type', value);
        },
        setFileManageBaseUrl: function (value) {
            setInStorage('fmanager-burl', value);
        },
        getFileManageBaseUrl: function () {
            getFromStorage('fmanager-burl');
        },
        setEnableCrop: function (value) {
            setInStorage('fmanager-enable-crop', value);
        },
        getEnableCrop: function () {
            getFromStorage('fmanager-enable-crop');
        },
        setCropRatio: function (value) {
            setInStorage('fmanager-crop-ratio', value);
        },
        getCropRatio: function () {
            getFromStorage('fmanager-crop-ratio');
        },
        setNeedRefresh: function (value) {
            setInStorage('need-refresh', value ? 'true' : 'false');
        },
        getNeedRefresh: function () {
            return getFromStorage('need-refresh') === 'true';
        },
        getEmptySeason: function (seasonInstance){
            return {
                id: (seasonInstance && seasonInstance.id) || null,
                seasonId: (seasonInstance && seasonInstance.seasonId) || null,
                count: (seasonInstance && seasonInstance.count) || 0,
                order: (seasonInstance && seasonInstance.order) || 0,
                finalPurchasedCount: (seasonInstance && seasonInstance.finalPurchasedCount) || 0,
                purchasedCount: (seasonInstance && seasonInstance.finalPurchasedCount) || 0,
                salesRestriction: (seasonInstance && seasonInstance.salesRestriction) || false,
                maxPurchasedPerUser: (seasonInstance && seasonInstance.maxPurchasedPerUser) || 0,
                maxPurchasedPeriodType: (seasonInstance && seasonInstance.maxPurchasedPeriodType) || 1,
                maxPurchasedPeriodCount: (seasonInstance && seasonInstance.maxPurchasedPeriodCount) || 0,
                salePlans: [this.getEmptySalePlan({
                    adultCount: ((seasonInstance && seasonInstance.selectedBusinessTypeIsReservable) || false) ? 1 : 0
                })]
            };
        },
        getEmptySalePlan: function (salePlaneInstance){
          return {
              id: (salePlaneInstance && salePlaneInstance.id) || null,
              realAmount: (salePlaneInstance && salePlaneInstance.realAmount) || 0,
              saleAmount: (salePlaneInstance && salePlaneInstance.saleAmount) || 0,
              discountRate: (salePlaneInstance && salePlaneInstance.discountRate) || 0,
              minPurchased: (salePlaneInstance && salePlaneInstance.minPurchased) || 1,
              adultCount: (salePlaneInstance && salePlaneInstance.adultCount) || 0,
              childCount: (salePlaneInstance && salePlaneInstance.childCount) || 0,
              description: ''
          };
        },
        dateToString: function (date) {
            isShamsi = siteConfig.calendarType === 1;
            var months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
            if (isShamsi) {
                months = ["فروردين", "ارديبهشت", "خرداد", "تير", "مرداد", "شهريور", "مهر", "آبان", "آذر", "دي", "بهمن", "اسفند"];
            }
            var dateArr = date.split('/');
            if (dateArr.length !== 2) return '';
            var month = parseInt(convertNumbers2English(dateArr[1]), 10);
            var year = isShamsi ? dateArr[0] : '20' + dateArr[0] ;
            var strDate = months[month - 1] + ' ' + year;
            return $translate.instant('common.paymentDateOn') + ' ' + strDate;
        },
        getDate: function (type) {

            isShamsi = siteConfig.calendarType === 1;

            var week = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];
            var months = ['January','February','March','April','May','June','July','August','September','October','November','December'];

            if(isShamsi) {
                week = ["يكشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنج شنبه", "جمعه", "شنبه"]
                months = ["فروردين", "ارديبهشت", "خرداد", "تير", "مرداد", "شهريور", "مهر", "آبان", "آذر", "دي", "بهمن", "اسفند"];
            }

            var a = new Date();
            var d = a.getDay();

            var today = moment().startOf('day');
            var todayCaption = today.local().format($rootScope.calendarType === 'jalali' ? ('j' + $rootScope.dateFormat.replaceAll('/', '/j')) : $rootScope.dateFormat);

            /*var day = a.getDate();
            var month = a.getMonth() + 1;
            var year = a.getYear();
            year = (year === 0) ? 2000 : year;
            (year < 1000) ? (year += 1900) : true;

            if(isShamsi) {
                day += 1;
                year -= ((month < 3) || ((month == 3) && (day < 21))) ? 622 : 621;

                switch (month) {
                    case 1:
                        (day < 21) ? (month = 10, day += 10) : (month = 11, day -= 20);
                        break;
                    case 2:
                        (day < 20) ? (month = 11, day += 11) : (month = 12, day -= 19);
                        break;
                    case 3:
                        (day < 21) ? (month = 12, day += 9) : (month = 1, day -= 20);
                        break;
                    case 4:
                        (day < 21) ? (month = 1, day += 11) : (month = 2, day -= 20);
                        break;
                    case 5:
                    case 6:
                        (day < 22) ? (month -= 3, day += 10) : (month -= 2, day -= 21);
                        break;
                    case 7:
                    case 8:
                    case 9:
                        (day < 23) ? (month -= 3, day += 9) : (month -= 2, day -= 22);
                        break;
                    case 10:
                        (day < 23) ? (month = 7, day += 8) : (month = 8, day -= 22);
                        break;
                    case 11:
                    case 12:
                        (day < 22) ? (month -= 3, day += 9) : (month -= 2, day -= 21);
                        break;
                    default:
                        break;
                }
            }*/

            var ptoday = '';

            if (type === 'longdate')
                ptoday = week[d] + '  ' + todayCaption;//day + '  ' + months[month - 1] + '  ' + year;
            else
                ptoday = todayCaption;//year + "/" + month + "/" + day;


            return ptoday;
        },
        getContainerTagList: function () {
            return ['div', 'section', 'article', 'main', 'aside','nav', 'none'];
        },
        getScreenTypeList: function () {
            return [
                {value: 0, text: 'هردو'},
                {value: 1, text: 'دسکتاپ'},
                {value: 2, text: 'موبایل'}
            ];
        },
        getSliderTypeList: function () {
            return [
                {value: '', text: 'simple'},
                {value: 'nearby-owl-slider', text: 'nearby slider'}
            ];
        },
        getDesktopSizeList: function () {
            return [
                {value: '', text: '----------'},
                {value: 'lsg-md-12', text: 'تمام صفحه (100%)'},
                {value: 'lsg-md-6', text: 'يک از دو (50%)'},
                {value: 'lsg-md-4', text: 'يک از سه (33%)'},
                {value: 'lsg-md-8', text: 'دو از سه (66%)'},
                {value: 'lsg-md-3', text: 'یک از چهار (25%)'},
                {value: 'lsg-md-9', text: 'سه از چهار (75%)'},
                {value: 'lsg-md-10', text: 'پنج از شش (83.3%)'},
                {value: 'lsg-md-2', text: 'یک از شش (16.6%)'},
                {value: 'lsg-md-11', text: 'یازده از دوازده (91.6%)'},
                {value: 'lsg-md-1', text: 'یک از دوازده (8.3%)'}
            ];
        },
        getTabletSizeList: function () {
            return [
                {value: '', text: '----------'},
                {value: 'lsg-sm-12', text: 'تمام صفحه (100%)'},
                {value: 'lsg-sm-6', text: 'يک از دو (50%)'},
                {value: 'lsg-sm-4', text: 'يک از سه (33%)'},
                {value: 'lsg-sm-8', text: 'دو از سه (66%)'},
                {value: 'lsg-sm-3', text: 'یک از چهار (25%)'},
                {value: 'lsg-sm-9', text: 'سه از چهار (75%)'},
                {value: 'lsg-sm-10', text: 'پنج از شش (83.3%)'},
                {value: 'lsg-sm-2', text: 'یک از شش (16.6%)'},
                {value: 'lsg-sm-11', text: 'یازده از دوازده (91.6%)'},
                {value: 'lsg-sm-1', text: 'یک از دوازده (8.3%)'}
            ];
        },
        getMobileSizeList: function () {
            return [
                {value: '', text: '----------'},
                {value: 'lsg-xs-12', text: 'تمام صفحه (100%)'},
                {value: 'lsg-xs-6', text: 'يک از دو (50%)'},
                {value: 'lsg-xs-4', text: 'يک از سه (33%)'},
                {value: 'lsg-xs-8', text: 'دو از سه (66%)'},
                {value: 'lsg-xs-3', text: 'یک از چهار (25%)'},
                {value: 'lsg-xs-9', text: 'سه از چهار (75%)'},
                {value: 'lsg-xs-10', text: 'پنج از شش (83.3%)'},
                {value: 'lsg-xs-2', text: 'یک از شش (16.6%)'},
                {value: 'lsg-xs-11', text: 'یازده از دوازده (91.6%)'},
                {value: 'lsg-xs-1', text: 'یک از دوازده (8.3%)'}
            ];
        },
        getLinkedboxShowTypeList: function () {
            return [{id: 1, name: 'افقی'}, {id: 2, name: 'عمودی'}];
        },
        getListGeneratorTypeList: function () {
            var projectType = httpServices.getProjectType();
            var key = 1;
            var _name = 'کروسل محصول';
            if(projectType === 'advertise'){
                key = 13;
                _name = 'کروسل تخفیف';
            }
            return [
                {id: -1, name: 'سطر چند ستونی'},
                {id: 0, name: 'جعبه متنی'},
                {id: key, name: _name},
                {id: 2, name: 'لینک باکس'},
                {id: 3, name: 'منو'},
                {id: 4, name: 'کاربران'},
                {id: 5, name: 'سبد خرید'},
                {id: 6, name: 'جستجوی محصولات'},
                {id: 7, name: 'تماس با ما'},
                {id: 8, name: 'منو کشویی'},
                {id: 9, name: 'جستجوی اکاریونی'},
                {id: 10, name: 'کاربران(فروریز)'},
                {id: 11, name: 'اسلایدر'},
                {id: 12, name: 'محتوا ساز'},
                {id: 14, name: 'لیست زبان'},
                {id: 15, name: 'انتخاب آدرس'},
                {id: 16, name: 'کروسل فروشگاه'},
                {id: 18, name: 'کروسل مقالات'},
                {id: 19, name: 'لیست مقالات'}
            ];
        },
        getListGeneratorTypeById: function (id) {
            var retObj = {id: -1, name: 'نامعلوم'};
            var find = false;
            this.getListGeneratorTypeList().forEach(function(listGeneratorType, index) {
                if(find === false && listGeneratorType.id === id){
                    find = true;
                    retObj = listGeneratorType;
                }
            });
            return retObj;
        },
        getLinkTargets: function () {
            return [
                {value: '', text: '------'},
                {value: '_blank', text: 'صفحه جدید'}
            ];
        },
        setMapLang: function(val,callback){
            localStorage.setItem('maplanguage', val);
            var lang = val;
            //Destroy old API
            document.querySelectorAll('script[src^="https://maps.googleapis.com"]').forEach(script => {
                script.remove();
            });

            if (google) delete google.maps;
            var script = document.createElement('script');
            script.type = 'text/javascript';
            script.src = 'https://maps.googleapis.com/maps/api/js?v=3.exp&key=AIzaSyDSGJmpiD5LHUNvCGZKfvMmTrpemLGm6oI&language=' + lang + '&region=IR';
            script.id = "google-maps-script";
            script.onload = callback;
            document.body.appendChild(script);
        },
        getMapLang: function(){
            var lang = localStorage.getItem('maplanguage');
            return (lang === "" || lang === undefined) ? "fa" : lang;
        },
        getGendersList: function () {
            return [
                {id: 1, name: 'common.male'},
                {id: 2, name: 'common.female'}
            ];
        },
        getBankGetways: function () {
            return [{ 'id': 2, 'name': 'common.mellatBankPayment' }];
        },
        getApprovedState: function () {
            return [{ 'id': -1, 'name': 'common.all' },{ 'id': 0, 'name': 'common.pending' },{ 'id': 1, 'name': 'common.verify' },{ 'id': 2, 'name': 'common.notVerify' }];
        },
        getSettlementState: function () {
            return [{ 'id': -1, 'name': 'common.all' },{ 'id': 0, 'name': 'common.notSettled' },{ 'id': 1, 'name': 'common.settled' }];
        },
        getShipmentPlanStatus: function () {
            return [
                { id: -2, name: 'common.all', scopeId : 0},
                { id: -1, name: 'common.awaitingPayment', scopeId : 1},
                { id: 0, name: 'common.awaitingVerify', scopeId : 1},
                { id: 1, name: 'common.verified', scopeId : 0},
                { id: 2, name: 'common.rejected', scopeId : 0},
                { id: 3, name: 'common.sended', scopeId : 1},
                { id: 4, name: 'common.delivered', scopeId : 1},
                { id: 5, name: 'common.customerNotDelivered', scopeId : 1},
                { id: 6, name: 'common.canceledByCustomer', scopeId : 0},
                { id: 7, name: 'common.returned', scopeId : 1}
            ];
        },
        getShipmentPaymentTypes: function () {
            return [
                { id: -1, name: 'common.all'},
                { id: 1, name: 'common.payOnline'},
                { id: 2, name: 'common.paymentOnSpot'},
                { id: 3, name: 'common.paymentByCredit'},
                { id: 4, name: 'common.payCreditDeficitOnline'}
            ];
        },
        getShipmentTypes: function () {
            return [
                { id: -1, name: 'common.all'},
                { id: 1, name: 'common.sendByCourier'},
                { id: 2, name: 'common.sendByPost'},
                { id: 3, name: 'common.deliveryInStore'}
            ];
        },
        getDateRangs: function () {
            return [
                { id: -2, name: 'common.all'},
                { id: -1, name: 'common.beforeToday'},
                { id: 0, name: 'common.today'},
                { id: 1, name: 'common.tomorrowe'},
                { id: 2, name: 'common.afterTomorrowe'}
            ];
        },
        getSortList: function () {
            return [
                {id: 4, name: 'common.newest'},
                {id: 1, name: 'common.mostPopular'},
                {id: 2, name: 'common.mostVisited'},
                {id: 3, name: 'common.mostSold'},
                {id: 5, name: 'common.cheapest'},
                {id: 6, name: 'common.mostExpensive'}
            ];
        },
        getPeriodList: function () {
            return [
                { id: 1, name: 'common.day' },
                { id: 2, name: 'common.month' }
            ];
        },
        getBookingPeriodList: function () {
            return [
                { id: 0,name: 'shop.bookingPeriodType'},
                { id: 1, name: 'common.minute' },
                { id: 2, name: 'common.hour' },
                { id: 3, name: 'common.day' }
            ];
        },
        getDayTypeList: function () {
            return [
                { id: 1, name: 'common.day' },
                { id: 2, name: 'common.workDay' }
            ];
        },
        getPricingModeList: function () {
            return [
                { id: 1, name: 'common.fix' },
                { id: 2, name: 'common.dynamic' }
            ];
        },
        getRejectItemState: function () {
            return [
                { id: 1, name: 'order.healthyProduct' },
                { id: 2, name: 'order.damagedProduct' },
                { id: 3, name: 'order.errorSending' }
            ];
        },
        getAppOrientationList: function () {
            return [
                { id: '', name: 'pwa.followDeviceOrientation' },
                { id: 'portrait', name: 'pwa.portrait' },
                { id: 'landscape', name: 'pwa.landscape' }
            ];
        },
        getAppDisplayList: function () {
            return [
                { id: 'fullscreen', name: 'pwa.fullscreen' },
                { id: 'standalone', name: 'pwa.standalone' },
                { id: 'minimal-ui', name: 'pwa.minimal-ui' }
            ];
        },
        getTrueFalseList: function () {
            return [
                { value: false, caption: 'common.no' },
                { value: true, caption: 'common.yes' }
            ];
        },

        getBztScopeList: function () {
            return [
                {id: -1, name: 'انتخاب حوزه عملیاتی'},
                {id: 0, name: 'هیچکدام'},
                {id: 1, name: 'کالا'},
                {id: 2, name: 'خدمات'}
            ];
        },

        getTaxableList: function () {
            return [
                {id: 0, name: 'تعلق نمی گیرد'},
                {id: 1, name: 'تعلق می گیرد'}
            ];
        },

        getProductWeightTypeList: function () {
            return [
                { id: 0, name: 'هیچکدام' },
                { id: 1, name: 'سبک وزن' },
                { id: 2, name: 'سنگین وزن' },
                { id: 3, name: 'ارسال سریع' }
            ];
        },

        getPriceCalcTypeList: function () {
            return [
                { id: 1, name: 'قیمت ثابت' },
                { id: 2, name: 'فرمول قیمت طلا' },
                { id: 3, name: 'هر دو' }
            ];
        },

        getBasePriceGroupList: function () {
            return [
                { id: 0, name: 'هیچکدام' },
                { id: 1, name: 'قیمت انواع طلا' },
                { id: 2, name: 'قیمت انواع نقره' }
            ];
        },
        getMessageResourceTypeList: function () {
            return [
                { id: 1, name: 'common.app' },
                { id: 2, name: 'common.panel' },
                { id: 3, name: 'common.site' }
            ];
        },
        getRoleTypeList: function () {
            return [
                { id: 1, name: 'مدیر' },
                { id: 2, name: 'کاربر' },
            ];
        },
        getLoginType: function () {
            return [
                { id: null, name: 'پیش فرض سیستم' },
                { id: 0, name: 'یکبار رمز' },
                { id: 1, name: 'رمزعبور' },
                { id: 2, name: 'رمزعبور و یکباررمز' },
            ];
        },
        getCalcTypeList: function () {
            return [
                { id: 1, name: 'common.byPrecent' },
                { id: 2, name: 'common.byValue' },
            ];
        },
        getAffectTypeList: function () {
            return [
                { id: 1, name: 'common.incremental' },
                { id: 2, name: 'common.decremental' },
            ];
        },
        getWageTypeList: function () {
            return [
                { id: 0, name: 'wageType.none' },
                { id: 1, name: 'wageType.precent' },
                { id: 2, name: 'wageType.amount' },
            ];
        },
        getOperationSourceTypeList: function () {
            return [
                { id: 0, name: 'accountingOperation.both' },
                { id: 1, name: 'accounting.wallet' },
                { id: 2, name: 'accounting.bankCard' },
            ];
        },
        getBaseType: function () {
            return [
                { id: 1, name: 'common.none' },
                { id: 2, name: 'common.site' },
                { id: 3, name: 'shop.branch' },
            ];
        },
        getExtraServicePriceCalcTypes: function () {
            return [
                { id: 1, name: 'shop.perPerson'},
                { id: 2, name: 'shop.perOrderItemCount'}
            ];
        },
        getCreditTypeList: function () {
            return [
                { id: 1, name: 'accounting.prepaid'},
                /*{ id: 2, name: 'accounting.postpaid'},
                { id: 3, name: 'accounting.revolvingPostpaid'}*/
            ];
        },
        getCreditViewTypeList: function () {
            return [
                { id: 1, name: 'common.single'},
                { id: 2, name: 'group'}
            ];
        },
        getStateList: function () {
            return [
                { id: 0, name: 'common.pending' },
                { id: 1, name: 'common.published' },
            ];
        },
        getGlobalStatusList: function () {
            return [
                { value: 1, caption: 'common.active' },
                { value: 0, caption: 'common.inactive' },
            ];
        },
        getOperatorList: function () {
            return [
                { name: 'MCI', caption: 'همراه اول' },
                { name: 'MTN', caption: 'ایرانسل' },
                { name: 'RAY', caption: 'رایتل' },
            ];
        },
        getDbxUserCols: function (){
            return [
                {
                    name: 'id',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: this.dataType.int
                },
                {
                    name: 'username',
                    title: 'common.userName',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3'
                },
                {
                    name: 'firstName',
                    title: 'common.name',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-4'
                },
                {
                    name: 'lastName',
                    title: 'common.family',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-4'
                },
            ]
        },
        getDbxMerchantUserCols: function (){
            return [
                {
                    name: 'customerUserId',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: this.dataType.int
                },
                {
                    name: 'maskCustomerUserName',
                    title: 'common.userName',
                    allowSorting: true,
                    cellClass: 'text-center ltrdir',
                    headerClass: 'text-center col-md-3'
                },
                {
                    name: 'fullName',
                    title: 'common.name',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-4'
                },
                {
                    name: 'createDate',
                    title: 'common.createDate',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-4'
                },
            ]
        },
        getDbxMerchantCols: function (){
            return [
                {
                    name: 'id',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: this.dataType.int
                },
                {
                    name: 'userName',
                    title: 'common.userName',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3'
                },
                {
                    name: 'name',
                    title: 'common.name',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3'
                },
                {
                    name: 'merchantCategoryName',
                    title: 'common.type',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3'
                },
                {
                    name: 'cityName',
                    keyName: 'cityId',
                    title: 'common.city',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3',
                    dataType: this.dataType.list,
                    filterList: Emix.Api.Application.global.getCityList,
                    filterListKey: 'id',
                    filterListValue: 'name',
                },
            ]
        },
        getTimeTableDays: function () {
            return [
                {
                    dayIndex: 1, name: 'common.week.saturday', isOpen: true,
                    settings: {
                        dropdownToggleState: false,
                        time: {
                            fromHour: '08',
                            fromMinute: '00',
                            toHour: '23',
                            toMinute: '00'
                        },
                        noRange: false,
                        format: 24,
                        noValidation: false
                    }
                },
                {
                    dayIndex: 2, name: 'common.week.sunday', isOpen: true,
                    settings: {
                        dropdownToggleState: false,
                        time: {
                            fromHour: '08',
                            fromMinute: '00',
                            toHour: '23',
                            toMinute: '00'
                        },
                        noRange: false,
                        format: 24,
                        noValidation: false
                    }
                },
                {
                    dayIndex: 3, name: 'common.week.monday', isOpen: true,
                    settings: {
                        dropdownToggleState: false,
                        time: {
                            fromHour: '08',
                            fromMinute: '00',
                            toHour: '23',
                            toMinute: '00'
                        },
                        noRange: false,
                        format: 24,
                        noValidation: false
                    }
                },
                {
                    dayIndex: 4, name: 'common.week.tuesday', isOpen: true,
                    settings: {
                        dropdownToggleState: false,
                        time: {
                            fromHour: '08',
                            fromMinute: '00',
                            toHour: '23',
                            toMinute: '00'
                        },
                        noRange: false,
                        format: 24,
                        noValidation: false
                    }
                },
                {
                    dayIndex: 5, name: 'common.week.wednesday', isOpen: true,
                    settings: {
                        dropdownToggleState: false,
                        time: {
                            fromHour: '08',
                            fromMinute: '00',
                            toHour: '23',
                            toMinute: '00'
                        },
                        noRange: false,
                        format: 24,
                        noValidation: false
                    }
                },
                {
                    dayIndex: 6, name: 'common.week.thursday', isOpen: true,
                    settings: {
                        dropdownToggleState: false,
                        time: {
                            fromHour: '08',
                            fromMinute: '00',
                            toHour: '23',
                            toMinute: '00'
                        },
                        noRange: false,
                        format: 24,
                        noValidation: false
                    }
                },
                {
                    dayIndex: 7, name: 'common.week.friday', isOpen: true,
                    settings: {
                        dropdownToggleState: false,
                        time: {
                            fromHour: '08',
                            fromMinute: '00',
                            toHour: '23',
                            toMinute: '00'
                        },
                        noRange: false,
                        format: 24,
                        noValidation: false
                    }
                }
            ];
        },
        getServiceBundleList: function () {
            return[
                {
                    id: 1,
                    name: 'boronz',
                    title: "بسته برنزی",
                    price: 3000000,
                    uiClassName: 'bg-gray',
                    packageDetail: [
                        {
                            name: "نمایش لوگو کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش شماره تلفن کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش آدرس صفحه اینستاگرام",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش آدرس سایت کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش آدرس صفحه تلگرام کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش شماره واتس آپ کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش موقعیت فیزیکی بر روی نقشه",
                            price: 0,
                            value: true
                        },
                        {
                            name: "تعدا ثبت تخفیف برای کسب و کار در ماه",
                            price: 0,
                            value: 10
                        },
                        {
                            name: "صفحه اختصاصی فروشگاه همراه با اسلایدر",
                            price: 0,
                            value: true
                        },
                        {
                            name: "صفحه اختصاصی تخفیف فروشگاه",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش تخقیف جدید به صورت چرخشی در صفحه اصلی هر شهر",
                            price: 0,
                            value: false
                        },
                        {
                            name: "نمایش در لیست جدید ترین کسب و کارها در صقحه اصلی",
                            price: 0,
                            value: false
                        },
                        {
                            name: "نمایش لوگو یا تصاویر تخفیف ها لابلای مقالات مرتبط",
                            price: 0,
                            value: false
                        },
                        {
                            name: "نمایش لوگو با تصاویر تخفیف ها در باکس وب گردی زیر مقالات",
                            price: 0,
                            value: false
                        },
                        {
                            name: "طراحی و انتشار اسلایدر بزرگ در صفحه اصلی",
                            price: 0,
                            value: false
                        },
                        {
                            name: "قرار گرفتن در خبر نامه سایت و ارسال ایمیل برای کاربران سایت",
                            price: 0,
                            value: false
                        },
                        {
                            name: "تولید و انتشار آگهی رپورتاژ در سایت تیجور",
                            price: 0,
                            value: false
                        }
                    ]
                },
                {
                    id: 2,
                    name: 'silver',
                    title: "بسته نقره ای",
                    price: 4500000,
                    uiClassName: 'bg-silver',
                    packageDetail: [
                        {
                            name: "نمایش لوگو کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش شماره تلفن کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش آدرس صفحه اینستاگرام",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش آدرس سایت کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش آدرس صفحه تلگرام کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش شماره واتس آپ کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش موقعیت فیزیکی بر روی نقشه",
                            price: 0,
                            value: true
                        },
                        {
                            name: "تعدا ثبت تخفیف برای کسب و کار در ماه",
                            price: 0,
                            value: 30
                        },
                        {
                            name: "صفحه اختصاصی فروشگاه همراه با اسلایدر",
                            price: 0,
                            value: true
                        },
                        {
                            name: "صفحه اختصاصی تخفیف فروشگاه",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش تخقیف جدید به صورت چرخشی در صفحه اصلی هر شهر",
                            price: 0,
                            value: '3 روز'
                        },
                        {
                            name: "نمایش در لیست جدید ترین کسب و کارها در صقحه اصلی",
                            price: 0,
                            value: '5 روز'
                        },
                        {
                            name: "نمایش لوگو یا تصاویر تخفیف ها لابلای مقالات مرتبط",
                            price: 0,
                            value: false
                        },
                        {
                            name: "نمایش لوگو با تصاویر تخفیف ها در باکس وب گردی زیر مقالات",
                            price: 0,
                            value: false
                        },
                        {
                            name: "طراحی و انتشار اسلایدر بزرگ در صفحه اصلی",
                            price: 0,
                            value: '3 روز'
                        },
                        {
                            name: "قرار گرفتن در خبر نامه سایت و ارسال ایمیل برای کاربران سایت",
                            price: 0,
                            value: '200 ایمیل'
                        },
                        {
                            name: "تولید و انتشار آگهی رپورتاژ در سایت تیجور",
                            price: 0,
                            value: false
                        }
                    ]
                },
                {
                    id: 3,
                    name: 'gold',
                    title: "بسته طلایی",
                    price: 7500000,
                    uiClassName: 'bg-gold',
                    packageDetail: [
                        {
                            name: "نمایش لوگو کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش شماره تلفن کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش آدرس صفحه اینستاگرام",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش آدرس سایت کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش آدرس صفحه تلگرام کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش شماره واتس آپ کسب و کار",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش موقعیت فیزیکی بر روی نقشه",
                            price: 0,
                            value: true
                        },
                        {
                            name: "تعدا ثبت تخفیف برای کسب و کار در ماه",
                            price: 0,
                            value: 50
                        },
                        {
                            name: "صفحه اختصاصی فروشگاه همراه با اسلایدر",
                            price: 0,
                            value: true
                        },
                        {
                            name: "صفحه اختصاصی تخفیف فروشگاه",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش تخقیف جدید به صورت چرخشی در صفحه اصلی هر شهر",
                            price: 0,
                            value: '6 روز'
                        },
                        {
                            name: "نمایش در لیست جدید ترین کسب و کارها در صقحه اصلی",
                            price: 0,
                            value: '10 روز'
                        },
                        {
                            name: "نمایش لوگو یا تصاویر تخفیف ها لابلای مقالات مرتبط",
                            price: 0,
                            value: true
                        },
                        {
                            name: "نمایش لوگو با تصاویر تخفیف ها در باکس وب گردی زیر مقالات",
                            price: 0,
                            value: true
                        },
                        {
                            name: "طراحی و انتشار اسلایدر بزرگ در صفحه اصلی",
                            price: 0,
                            value: '10 روز'
                        },
                        {
                            name: "قرار گرفتن در خبر نامه سایت و ارسال ایمیل برای کاربران سایت",
                            price: 0,
                            value: '400 ایمیل'
                        },
                        {
                            name: "تولید و انتشار آگهی رپورتاژ در سایت تیجور",
                            price: 0,
                            value: true
                        }
                    ]
                }
            ];
        },
        getFinanceConfig: function (){
            return {
                showAccountNumber : false,
                showCartNumber : false,
                cartNumberMask : '9999-9999-9999-9999',
                showIbanNumber : false,
                ibanNumberMask : 'AA-999-99999-9999999999999999',
                showSwiftNumber : false,
                swiftNumberMask : 'AAAA-AA-**',
                showPaypalAccount : false,
            };
        }
    };
}]);