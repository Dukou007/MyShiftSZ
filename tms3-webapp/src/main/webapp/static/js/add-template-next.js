/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                Action
 * 2017-1-10 	         TMS_HZ           	Create/Add/Modify/Delete
 * ============================================================================		
 */
define(function(require, exports, module) {
    var TMS = require('TMS');
    require('validate-methods');
    require('datetimepicker');
    require('fileupload');
    $(function() {
        TMS.init();
        $.validator.setDefaults({});
        $("#add-template").validate({
            ignore: '',
            errorElement: 'li',
            errorPlacement: function(error, element) {
                var name = $(element).parent().siblings('.edit-name').text();
                error.insertAfter('#messageBox');
                error.text(name + " " + error.text());

            },
            submitHandler: function(form) {
                TMS.getData({
                    url: '',
                    type: 'post',
                    data: $(form).serialize()
                });
                return false;
            }
        });
        $('.datetimepicker').datetimepicker({
            autoclose: true
        });
        $('.fileupload').fileupload({
            dataType: 'json',
            url: _WEB_SITE_ + '/pkg/service/upload',
            type: 'put',
            autoUpload: false,
            recalculateProgress: false,
            formData: function(form) {
                return {};
            },
            add: function(e, data) {
                var maxFileSize = 200 * 1024 * 1024; //允许的文件最大值200MB
                $.each(data.files, function(index, file) {
                    var uploadErrors = [];
              
                    if (uploadErrors.length == 0) {
                        var ff = new FileReader(); //check the drag and drop is a folder(firefox)
                        ff.readAsArrayBuffer(file);
                        ff.onload = function() {

                            data.submit();

                        };
                        ff.onerror = function() {
                            TMS.alert('Package file is a folder', 'danger');
                        };
                    }

                });

            },
            done: function(e, data) { //上传完成

                $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.filename').val(data.result.filePath);
                TMS.alert(data.result.message, 'success');
            },
            fail: function(e, data) { //上传失败

                $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.filename').val('');
                TMS.alert(data.result.message, 'danger');

            },
            progress: function(e, data) {
              
            },
            progressall: function(e, data) { //进度条
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress .progress-bar').css(
                    'width',
                    progress + '%'
                );


            },
            start: function(e) { //开始时
                $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress').removeClass('hide');
            },
            always: function(e, data) { //默认上传结束执行
            },
            stop: function(e) {
                setTimeout(function() {
                    $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress').addClass('hide');
                    $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress .progress-bar').css(
                        'width', '0%'
                    );
                }, 500)

            }
        }).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled')
    });
})