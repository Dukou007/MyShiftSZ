(function(factory){
    if (typeof define === 'function')
      define(['jquery'], function(require,exports,moudles){
          factory(require('jquery')); 
          return jQuery;
      });
    else 
      factory(jQuery);

}(function( $ ) {

  $.bootstrapGrowl = function(message, options) {
    var $alert, css, offsetAmount;

    options = $.extend({}, $.bootstrapGrowl.default_options, options);
    $alert = $("<div>");
    $alert.attr("class", "alert");
    if (options.type) {
      $alert.addClass("alert-" + options.type);
    }
    if (options.allow_dismiss) {
      $alert.append("<span class=\"close\" data-dismiss=\"alert\">&times;</span>");
    }
    $alert.append(message);
    if (options.top_offset) {
      options.offset = {
        from: "top",
        amount: options.top_offset
      };
    }
    offsetAmount = options.offset.amount;
    css={
      "margin-bottom": 10,
      "display": "none"
    }
    $alert.css(css);
    if (options.width !== "auto") {
      $alert.css("width", options.width + "px");
    }

    if($('.bootstrap-growl').length==0){
      if(options.ele == "body"){
        $('body').append('<div class = "bootstrap-growl"></div>');
      }else{
        $(options.ele).append('<div class = "bootstrap-growl"></div>');
      }
      var _css={
        'position':'absolute',
      }
      _css[options.offset.from] = offsetAmount + 'px';
      $('.bootstrap-growl').css(_css);
      switch (options.align) {
        case "center":
          $('.bootstrap-growl').css({
            "left": "50%",
            "margin-left": "-" + ($alert.outerWidth() / 2) + "px"
          });
          break;
        case "left":
          $('.bootstrap-growl').css("left", "20px");
          break;
        default:
          $('.bootstrap-growl').css("right", "20px");
      }
    }
    $('.bootstrap-growl').append($alert);
    $alert.fadeIn();
    if (options.delay > 0) {
      $alert.delay(options.delay).fadeOut(function() {
        return $(this).alert("close");
      });
    }
    return $alert;
  };

  $.bootstrapGrowl.default_options = {
    ele: "body",
    type: "info",
    offset: {
      from: "top",
      amount: 20
    },
    align: "right",
    width: 250,
    delay: 4000,
    allow_dismiss: true,
    stackup_spacing: 10
  };

}));