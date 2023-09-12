/**
 * @license Copyright (c) 2003-2018, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see https://ckeditor.com/legal/ckeditor-oss-license
 */

CKEDITOR.editorConfig = function (config) {
    // Define changes to default configuration here. For example:
    // config.language = 'fr';
    // config.uiColor = '#AADC6E';
    var currentLang = $.cookie(siteConfig.langCookieName) || 'fa_IR';
    config.language = currentLang.split('_')[0];
    config.toolbar = [
	//{ name: 'document', groups: ['mode', 'document', 'doctools'], items: ['Source', '-', 'Save', 'NewPage', 'Preview', 'Print', '-', 'Templates'] },
	//{ name: 'clipboard', groups: ['clipboard', 'undo'], items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo'] },
	//{ name: 'editing', groups: ['find', 'selection', 'spellchecker'], items: ['Find', 'Replace', '-', 'SelectAll'] },
    //{ name: 'paragraph', groups: ['list', 'indent', 'blocks', 'align', 'bidi'], items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl'] },
        { name: 'paragraph', groups: ['list', 'blocks', 'bidi'], items: ['NumberedList', 'BulletedList', '-', 'BidiLtr', 'BidiRtl'] },
    //{ name: 'links', items: ['Link', 'Unlink', 'Anchor'] },
	//'/',
    //{ name: 'styles', items: ['Styles', 'Format', 'Font', 'FontSize'] },
    { name: 'basicstyles', groups: ['basicstyles'], items: ['Bold', 'Italic', 'Underline'] },//{ name: 'basicstyles', groups: ['basicstyles', 'cleanup'], items: ['Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'CopyFormatting', 'RemoveFormat'] },
	//{ name: 'insert', items: ['Image', 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar'] },
	//{ name: 'colors', items: ['TextColor', 'BGColor'] },
	//{ name: 'tools', items: ['Maximize', 'ShowBlocks'] },
	//{ name: 'others', items: ['-'] },
	//{ name: 'about', items: ['About'] }
    ];
    modalFileman = '/nimdalenap/home/ngApp/pages/modal/file-manager.html';
    config.filebrowserBrowseUrl = modalFileman;
    config.filebrowserImageBrowseUrl = modalFileman + '?type=image';
    config.removeDialogTabs = 'link:upload;image:upload';

    config.contentsCss = [ siteConfig.fileDomain + '/assets/blog/css/all_b_bundled.css'];

    //config.extraPlugins = 'wordcount,notification,balloonpanel,a11ychecker,widget,image2';
    config.wordcount = {

        // Whether or not you want to show the Paragraphs Count
        showParagraphs: true,

        // Whether or not you want to show the Word Count
        showWordCount: true,

        // Whether or not you want to show the Char Count
        showCharCount: true,

        // Whether or not you want to count Spaces as Chars
        countSpacesAsChars: false,

        // Whether or not to include Html chars in the Char Count
        countHTML: false,

        // Maximum allowed Word Count, -1 is default for unlimited
        maxWordCount: -1,

        // Maximum allowed Char Count, -1 is default for unlimited
        maxCharCount: -1,

        // Add filter to add or remove element before counting (see CKEDITOR.htmlParser.filter), Default value : null (no filter)
        //filter: new CKEDITOR.htmlParser.filter({
        //    elements: {
        //        div: function (element) {
        //            if (element.attributes.class == 'mediaembed') {
        //                return false;
        //            }
        //        }
        //    }
        //})
    };
};
