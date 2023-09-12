//#################################################
//Mobile Menu #####################################
//#################################################


var menuLeft = document.getElementById('cbp-spmenu'),
            showLeft = document.getElementById('showLeft'),
            body = document.body;

showLeft.onclick = function () {
    classie.toggle(this, 'active');
    classie.toggle(menuLeft, 'cbp-spmenu-open');
    disableOther('showLeft');
};

function disableOther(button) {
    if (button !== 'showLeft') {
        classie.toggle(showLeft, 'disabled');
    }
}



$(window).load(function () {
    $('.flexslider').flexslider({
        animation: "slide",
        controlNav: false,
        slideshow: false,
        controlsContainer: ".flexslider-container"
    });
});


$(document).ready(function () {


    $('#orderLink,#moborderLink').click(function () {
        var productLink = $('#orderLink');

        productLink.attr("target", "_blank");
        window.open(productLink.attr("href"));

        return false;

    });
    //#################################################
    //Navigation - Scrolling and Active States#########
    //#################################################


    $('#sections, #sections_mobile').onePageNav({
        currentClass: 'current',
        changeHash: false,
        scrollSpeed: 1000,
        scrollOffset: 77,
        scrollThreshold: 0.2,
        easing: 'easeInOutExpo',

    });

    $('#logo a').click(function () {
        $('body,html').animate({
            scrollTop: 0
        }, 800);
        return false;
    });


    $('#btn-arrow').click(function () {
        $.scrollTo('#about', 800, { easing: 'easeInOutExpo', offset: -77, 'axis': 'y' });

    });

    //#################################################
    //Elements fading in ##############################
    //#################################################        


    setTimeout(function () { $('.btn').addClass('anim'); }, 1000);

    $('#about').waypoint(function (direction) {
        $('#about').animate({ opacity: 1 }, 1500);
        //setTimeout(function () { $('#clients').animate({ opacity: 1 }, 1500); }, 500);
    }, { offset: '50%' });

    $('#clients').waypoint(function (direction) {
        $('#clients').animate({ opacity: 1 }, 1500);
    }, { offset: '50%' });

    $('#platform').waypoint(function (direction) {
        $('#platform').animate({ opacity: 1 }, 1500);        
    }, { offset: '50%' });

    $('#folio').waypoint(function (direction) {
        $('#folio').animate({ opacity: 1 }, 1500);
    }, { offset: '50%' });

    $('#FAQ').waypoint(function (direction) {
        $('#FAQ').animate({ opacity: 1 }, 1500);
    }, { offset: '50%' });    

    function sniffer() {
        var windowHeight = $(window).height();

        var modal = $(".md-content");
        modal.css("height", windowHeight + "px");
        modal.css("direction", "ltr");


    };
    sniffer();


    //#################################################
    //Text animation on home ##########################
    //#################################################          

    var $tlt = $('.tlt').textillate({
        selector: '.texts',
        loop: true,
        autoStart: false,
        initialDelay: 10,
        'in': {
            effect: 'fadeInDown',
            delayScale: 1.3,
            delay: 40,
            sync: false,
            shuffle: true
        },
        'out': {
            effect: 'fadeOutDown',
            delayScale: 1.3,
            delay: 40,
            sync: false,
            shuffle: true
        },

        type: 'word'

    });


    $('#home p').textillate({
        loop: false,
        'in': {
            effect: 'fadeInUp',
            delayScale: 0.5,
            delay: 10,
            sync: false,
            shuffle: true,
        },

        type: 'word'

    });

    var index = 0;
    $(function () {
        var demo1 = $("#demo1").slippry({
            transition: 'horizontal',// fade, horizontal, vertical, kenburns, false
            kenZoom: 100, // max zoom for kenburns (in %)
            useCSS: true,
            speed: 2000,
            pause: 5000,
            auto: true,
            captions: 'false', // Position: overlay, below, custom, false
            preload: 'visible',
            autoHover: false,
            adaptiveHeight: true, // height of the sliders adapts to current slide
            onSlideAfter: function (slide, old_index, new_index) {
                index = (index + 1) % 3;
                $tlt.textillate('in', index);
            },
            onSlideBefore: function (slide, old_index, new_index) {
                $tlt.textillate('out');
            }

        });
    });

    //#################################################
    //ContactForm #####################################
    //#################################################       


    function contactform() {
        //contact form init
        var options = { target: "#alert" }
        $("#contact-form").ajaxForm(options);
    };
    contactform();




    //#################################################
    //Video for modal box #############################
    //#################################################          


    $(".md-content").fitVids();

});



//#################################################
//#################################################
//#################################################


$(document).ready(function () {
    navposition();
    $(window).on('resize', navposition);
});



function navposition() {
    var sliderHeight = $(".slides").height() / 2;
    var sliders = $(".flex-direction-nav li a");
    sliders.css("margin-top", -sliderHeight + 19 + "px");
};
navposition();


