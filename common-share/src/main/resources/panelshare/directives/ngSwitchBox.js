angular.module('emixApp.directives').directive('ngSwitchBox', function () {
    'use strict';
    return {
        restrict: 'AE'
        , replace: true
        , transclude: true
        , template: function(element, attrs) {
            var html = '';
            html += '<div class="ngswitchboxcontainer' + (attrs.containerclass ? ' ' + attrs.containerclass : '') + '">';
            html += '<span class="ngswitchboxtext">';
            html += attrs.text;
            html += '</span>';
            html += '<span';
            html +=   ' class="ngswitchbox' + (attrs.class ? ' ' + attrs.class : '') + '"';
            html +=   attrs.ngModel ? ' ng-click="' + attrs.disabled + ' ? ' + attrs.ngModel + ' : ' + attrs.ngModel + '=!' + attrs.ngModel + (attrs.ngChange ? '; ' + attrs.ngChange + '()"' : '"') : '';
            html +=   ' ng-class="{ checked:' + attrs.ngModel + ', disabled:' + attrs.disabled + ' }"';
            html +=   '>';
            html +=   '<small></small>';
            html +=   '<input type="checkbox"';
            html +=     attrs.id ? ' id="' + attrs.id + '"' : '';
            html +=     attrs.name ? ' name="' + attrs.name + '"' : '';
            html +=     attrs.ngModel ? ' ng-model="' + attrs.ngModel + '"' : '';
            html +=     ' style="display:none" />';
            html +=     '<span class="ngswitchbox-text">'; /*adding new container for ngswitchbox text*/
            html +=     attrs.on ? '<span class="on">'+attrs.on+'</span>' : ''; /*ngswitchbox text on value set by user in directive html markup*/
            html +=     attrs.off ? '<span class="off">'+attrs.off + '</span>' : ' ';  /*ngswitchbox text off value set by user in directive html markup*/
            html += '</span>';
            html += '</div>';
            return html;
        }
    }
});

