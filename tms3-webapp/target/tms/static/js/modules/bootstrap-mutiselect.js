 define(function(require, exports, module) {
  require('jquery');
  require('bootstrap');
  var mutiSelect=function(option){
  	this.option = $.extend({}, option, mutiSelect.option);
  	this.init();
  }
  mutiSelect.option={
  	el:'.mult-select'
  }
  mutiSelect.prototype={
  	init:function(){
  		this.renderHtml();
  		this.bindEvent();
  	},
  	renderHtml:function(){
  		var self = this;
  		$(self.option.el).each(function(index, el) {
  			var arr = [],temp=[];
  			
/*        $(el).find('optgroup').each(function(index, el) {

          item[$(el).attr('label')]={};
          arr.push(item)
        });*/
        $(el).find('option').each(function(index, el) {
          var item ={};
          item.text=$(el).text();
          item.value = $(el).attr('value');
          if($(el).attr('selected') == 'selected'){
            item.active = true;
          }else{
            item.active = false;
          }
          if($(el).parents('optgroup').length>0){
            item.parent=$(el).parents('optgroup').attr('label');
          }else{
            item.parent=null;
          }
          arr.push(item);
        });
        var optgroup = [],oldv;
        for(var i=0;i<arr.length;i++){

          if(arr[i].parent && oldv != arr[i].parent){
            optgroup.push(arr[i].parent);
            oldv = arr[i].parent;
          }
        }
        console.log(arr)
  			temp.push('<div class="mult-select-btn '+$(el).attr('class')+'">');
  			temp.push('<button type="button" class="btn btn-default dropdown-toggle">');
  			temp.push('<span class="mult-select-text">');
  			var text ='';
  			for(var i=0;i<arr.length;i++){
  				if(arr[i].active == true){
  					text+=arr[i].text+','
  				}
  			}
  			if(text){
  				text = text.substring(0,text.length-1);
  			}
  			temp.push(text);
  			temp.push('</span>');
  			temp.push('<span class="caret"></span>');
  			temp.push('</button>');
  			temp.push('<ul class="dropdown-menu muti-select-menu" >');
        if(optgroup.length>0){
          for(var i = 0;i<optgroup.length;i++){
            temp.push('<li class="muti-select-optgroup"><span class="glyphicon glyphicon-chevron-up muti-select-slideicon"></span>'+optgroup[i]+'<ul class="muti-select-optgroupitem">');
            for(var j=0;j<arr.length;j++){
              if(arr[j].parent == optgroup[i]){
                var cls ='';
                if(arr[j].active == true){
                  cls = 'active'
                }
                temp.push('<li class="'+cls+' muti-select-item"  data-val="'+arr[j].value+'">'+arr[j].text+'<span class="glyphicon glyphicon-ok muti-select-icon"></span></li>');
              }
            }
            temp.push('</ul></li>')

          }
        }else{
          for(var i=0;i<arr.length;i++){
            var cls ='';
            if(arr[i].active == true){
              cls = 'active'
            }
            temp.push('<li class="'+cls+' muti-select-item"  data-val="'+arr[i].value+'">'+arr[i].text+'<span class="glyphicon glyphicon-ok muti-select-icon"></span></li>');
          }
          temp.push('</ul>');
          temp.push('</div>');                  
         
        }
  			$(el).parent().append(temp.join('')); 
        $(el).hide();				
  		});
  	},
  	bindEvent:function(){
  		var self = this;
  		$(document).on('click', '.mult-select-btn>button', function(e) {
  			var $target = $(this).parent().find('.dropdown-menu');
			$target.css({'width':$(this).outerWidth()+'px','left':'15px'});
			$target.toggle();
			e.stopPropagation();
  		});
  		$(document).on('click', '.muti-select-item', function(e) {

  			$(this).toggleClass('active');
  			self.renderText($(this).parents('.mult-select-btn'));
  			e.stopPropagation();
  		});
      $(document).on('click', '.muti-select-menu', function(e) {
        e.stopPropagation();
      });
  		$(document).on('click', 'body', function() {
  			$('.muti-select-menu').hide();
  		});
      $(document).on('click', '.muti-select-slideicon', function() {
        $(this).parents('.muti-select-optgroup').find('.muti-select-optgroupitem').toggle();
        $(this).toggleClass('muti-select-slideopen');
      });
  	},
  	renderText:function($el){
  		var str = '',arr = [];
  		$el.find('.muti-select-item').each(function(index, el) {
  			if($(el).hasClass('active')){
  				str += $(el).text()+',';
  				console.log($el.siblings('select.mult-select'))
  				arr.push($(el).attr('data-val'));
  			}else{

  			}
  		});
  		if(str){
  			str = str.substring(0,str.length-1)
  		}
  		$el.find('.mult-select-text').text(str);
  		$el.siblings('select.mult-select').val(arr);
  	}
  }
  return mutiSelect;
});