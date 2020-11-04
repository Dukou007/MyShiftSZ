 (function(factory) {
     if (typeof define === 'function')
         define(function(require, exports, moudles) {
             require('jquery');
             require('validate');
             factory(jQuery);
             return jQuery;
         });
     else
         factory(jQuery);

 }(function($) {
     $.validator.addMethod("percentonly", function(value, element) {
         return this.optional(element) || /^[0-9]+(|.[0-9]+)$/.test(value);
     }, "The end time must not be less than the start time.");
     $.validator.addMethod("globalpercentonly", function(value, element) {
         return this.optional(element) || /^(((\d|[1-9]\d)(\.\d{1,2})?)|100|100.0|100.00)$/.test(value);
     }, "The end time must not be less than the start time.");
     $.validator.addMethod("keyindexonly", function(value, element) {
         return this.optional(element) || (/^[0-9]+$/.test(value) && value <= 10 && value > 0);
     }, "Please enter a positive integer from 1 to 10.");
     $.validator.addMethod("alphanumeric", function(value, element) {
         return this.optional(element) || /^\w+$/i.test(value);
     }, "Letters, numbers, and underscores only please.");
     $.validator.addMethod("amount", function(value, element) {
         return this.optional(element) || /^[0-9]+.[0-9]{2}$/.test(value);
     }, "Please enter a number and leave 2 digits after the decimal point.");
     $.validator.addMethod("numberslettersonly", function(value, element) {
         return this.optional(element) || /^[0-9A-Za-z]+$/i.test(value);
     }, "Only support numbers 0-9 and letters a-z and A-Z.");
     $.validator.addMethod("iponly", function(value, element) {
         return this.optional(element) || /^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])$/.test(value);
     }, "Please enter the correct ip address.");
 /*    $.validator.addMethod("regex_group", function(value, element) {
         return this.optional(element) || !(/[/\."<>{}()=]|\\$$/).test(value);
     }, 'please enter a valid group name.');*/
     $.validator.addMethod("lettersonly", function(value, element) {
         return this.optional(element) || /^[a-z]+$/i.test(value);
     }, "Letters only please");
    /* $.validator.addMethod("citycheck", function(value, element) {
         return this.optional(element) || /^(?!\d+$)[^\s][\w\s-]+$/.test(value);
     }, "please enter a valid city.");*/
     $.validator.addMethod("citycheck", function(value, element) {
    	  return this.optional(element) || !(/[/\<>]|\\$$/).test(value);
     }, "please enter a valid city.");
     $.validator.addMethod("lengthtype", function(value, element) {
         return this.optional(element) || /^(\w{8}|\w{10})$/.test(value);
     }, "Only support 8 , 10 characters.");

     $.validator.addMethod("nowhitespace", function(value, element) {
         return this.optional(element) || /^\S+$/i.test(value);
     }, "No white space please.");

     $.validator.addMethod("phone", function(phone_number, element) {
         phone_number = phone_number.replace(/\s+/g, "");
         return this.optional(element) || phone_number.match(/^(\+)?(\d|-|\s)*$/);
     }, "Please specify a valid phone number.");

     $.validator.addMethod("email", function(email, element) {
         email = email.replace(/\s+/g, "");
         return this.optional(element) || email.match(/[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/);
     }, "Please specify a valid email.");             

     jQuery.validator.addMethod("stateUS", function(value, element, options) {
             var isDefault = typeof options === "undefined",
                 caseSensitive = (isDefault || typeof options.caseSensitive === "undefined") ? false : options.caseSensitive,
                 includeTerritories = (isDefault || typeof options.includeTerritories === "undefined") ? false : options.includeTerritories,
                 includeMilitary = (isDefault || typeof options.includeMilitary === "undefined") ? false : options.includeMilitary,
                 regex;

             if (!includeTerritories && !includeMilitary) {
                 regex = "^(A[KLRZ]|C[AOT]|D[CE]|FL|GA|HI|I[ADLN]|K[SY]|LA|M[ADEINOST]|N[CDEHJMVY]|O[HKR]|PA|RI|S[CD]|T[NX]|UT|V[AT]|W[AIVY])$";
             } else if (includeTerritories && includeMilitary) {
                 regex = "^(A[AEKLPRSZ]|C[AOT]|D[CE]|FL|G[AU]|HI|I[ADLN]|K[SY]|LA|M[ADEINOPST]|N[CDEHJMVY]|O[HKR]|P[AR]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY])$";
             } else if (includeTerritories) {
                 regex = "^(A[KLRSZ]|C[AOT]|D[CE]|FL|G[AU]|HI|I[ADLN]|K[SY]|LA|M[ADEINOPST]|N[CDEHJMVY]|O[HKR]|P[AR]|RI|S[CD]|T[NX]|UT|V[AIT]|W[AIVY])$";
             } else {
                 regex = "^(A[AEKLPRZ]|C[AOT]|D[CE]|FL|GA|HI|I[ADLN]|K[SY]|LA|M[ADEINOST]|N[CDEHJMVY]|O[HKR]|PA|RI|S[CD]|T[NX]|UT|V[AT]|W[AIVY])$";
             }

             regex = caseSensitive ? new RegExp(regex) : new RegExp(regex, "i");
             return this.optional(element) || regex.test(value);
         },
         "Please specify a valid state");

     $.validator.addMethod("zipcodeUS", function(value, element) {
         return this.optional(element) || /^\d{5}(-\d{4})?$/.test(value);
     }, "The specified US ZIP Code is invalid.");
     
     $.validator.addMethod("usZipcode", function(value, element) {
         return this.optional(element) || /^[0-9]{5}$/.test(value);
     }, "please enter a valid zip/postal code, like:11111");
     
     $.validator.addMethod("canadaZipcode", function(value, element) {
         return this.optional(element) || /^[A-Za-z][0-9][A-Za-z]\s{0,1}[0-9][A-Za-z][0-9]$/.test(value);
     }, "please enter a valid zip/postal code, like:A1A 1A1");
     
     $.validator.addMethod("numberslettersonly-", function(value, element) {
         return this.optional(element) || /^[0-9 A-Za-z \-]+$/i.test(value);
     }, "Letters,-, numbers only please.");
     $.validator.addMethod("numbersletters", function(value, element) {
         return this.optional(element) || /^[0-9 A-Z]+$/.test(value);
     }, "Numeric and uppercase letters only please.");

     $.validator.addMethod("percentegeonly", function(value, element) {
         return this.optional(element) || (/^[0-9]+$/.test(value) && value < 100 && value > 0);
     }, "Please enter a positive integer from 1 to 99.");

     $.validator.addMethod("letterSCHP", function(value, element) {
         return this.optional(element) || /^[\'\.\,\-\sa-zA-Z]+$/g.test(value);
     }, "letters,spaces,commas,hyphens,periods only please.");

     $.validator.addMethod("andSoOn", function(value, element) {
         return this.optional(element) || !(/[^\s\(\)\[\]\{\}\*\&\$\^\%\#\@\!\~\`\,\.\;\:\'\"\\?\.\<\>\+\=\|\/\\\w_-]/g.test(value));
     }, "Must be 50 characters or less.");

     $.validator.addMethod("passwordCheck", function(value, element) {
         return this.optional(element) || /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\(\)\[\]\{\}\*\&\$\^\%\#\@\!\~\`\,\.\;\:\'\"\\\?\/\<\>\|])[0-9a-zA-Z\(\)\[\]\{\}\*\&\$\^\%\#\@\!\~\`\,\.\;\:\'\"\\\?\/\<\>\|]{8,32}$/.test(value);
     }, "New password must contain at least 8 characters, including upper case letters, lower case letters, numbers and special characters.");

     $.validator.addMethod("regex_Amount", function(value, element) {
         return this.optional(element) || /^[0-9]+\.+[0-9]{2}$/.test(value);
     }, "Please enter a number and leave 2 digits after the decimal point.");
     $.validator.addMethod("regex_Float", function(value, element) {
         return this.optional(element) || /^[0-9]+\.+[0-9]+$/.test(value);
     }, "Please enter a number with a decimal point.");
     $.validator.addMethod("regex_String", function(value, element) {
         var flag = true;
         for (var i = 0; i < value.length; i++) {
             if (value.charCodeAt(i) < 32 || value.charCodeAt(i) > 127) {
                 flag = false;
                 break;
             }
         }
         return this.optional(element) || flag;
     }, "Please enter a displayable character with an ASCII code of 0x20 - 0x7F.");
     $.validator.addMethod("regex_Number", function(value, element) {
         return this.optional(element) || /^[0-9]+$/.test(value);
     }, "Please enter a positive integer");
     $.validator.addMethod("regex_Number_String", function(value, element) {
         return this.optional(element) || /^[0-9A-Za-z]+$/.test(value);
     }, "Only support numeric and letters");
     $.validator.addMethod("regex_IP", function(value, element) {
         return this.optional(element) || /^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])$/.test(value);
     }, "Please specify a valid ip address");
     $.validator.addMethod("regex_Port", function(value, element) {
         return this.optional(element) || (/^[0-9]+$/.test(value) && value <= 65535 && value > 0);
     }, "Please enter a positive integer from 1 to 65535.");
     $.validator.addMethod("regex_Port_1", function(value, element) {
         return this.optional(element) || (/^[0-9]+$/.test(value) && value <= 99 && value > 0);
     }, "Please enter a positive integer from 1 to 99.");
     $.validator.addMethod("specialInput", function(value, element) {
         return this.optional(element) || !(/[/\<>]|\\$$/).test(value);
     }, "invalid special symbol.");
     $.validator.addMethod("regex_Time_hhmm", function(value, element) {
         var flag = true;
         if (!/^[0-9]{2}:[0-9]{2}$/.test(value)) {
             flag = false
         } else {
             var date = value.split(':');
             if (date[0] > 23 || date[1] > 59) {
                 flag = false
             }
         }
         return this.optional(element) || flag;

     }, "Please enter hh:mm format.");
     $.validator.addMethod("regex_Time", function(value, element) {
         var flag = true;
         if (!/^[0-9]{2}:[0-9]{2}:[0-9]{2}$/.test(value)) {
             flag = false
         } else {
             var date = value.split(':');
             if (date[0] > 23 || date[1] > 59 || date[2] > 59) {
                 flag = false
             }
         }
         return this.optional(element) || flag;

     }, "Please enter hh:mm:ss format.");
     $.validator.addMethod("regex_Date", function(value, element) {
         var flag = true;
         if (!/^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/.test(value)) {
             flag = false
         } else {
             var date = value.split('/');
             if (date[0] > 12 || date[0] < 1 || date[1] > 31 || date[1] < 1) {
                 flag = false
             }
         }
         return this.optional(element) || flag;

     }, "Please enter MM/DD/YYYY format.");
     $.validator.addMethod("regex_DateTime", function(value, element) {
         var flag = true;
         if (!/^[0-9]{2}\/[0-9]{2}\/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}$/.test(value)) {
             flag = false
         } else {
             var date = value.split(' ');
             var f = date[0].split('/'),
                 s = date[1].split('/');
             if (f[0] > 12 || f[0] < 1 || f[1] > 31 || f[1] < 1 || s[0] > 23 || s[1] > 59 || s[2] > 59) {
                 flag = false
             }
         }
         return this.optional(element) || flag;

     }, "Please enter MM/DD/YYYY hh:mm:ss format.");
     $.validator.addMethod("regex_keyIndex", function(value, element) {
         return this.optional(element) || (/^[0-9]+$/.test(value) && value <= 10 && value > 0);
     }, "Please enter a positive integer from 1 to 10.");
     $.validator.setDefaults({
         highlight: function(element) {
             $(element).closest('.form-edit-group').removeClass('has-success');
             $(element).closest('.form-edit-group').addClass('has-error');
         },
         unhighlight: function(element) {
             $(element).closest('.form-edit-group').removeClass('has-error');
             //$(element).closest('.form-edit-group').addClass('has-success');
         },
         errorElement: 'span',
         errorClass: 'help-block',
         errorPlacement: function(error, element) {
             if ($(element).parent().hasClass('edit-value')) {
                 $(element).parent().append(error);
             } else {
                 error.insertAfter(element);
             }

         }
     });
 }))
