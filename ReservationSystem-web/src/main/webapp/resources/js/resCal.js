(function( $ ) {
    $.widget( "ui.resCal", {
        
        // These options will be used as defaults
        options: { 
            date: new Date(),
            data: null,
            locale: 'en',
            userName: null,
            daysToDisplay: 7,
            query: null,
            userId: null,
            hours: null,
            computerId: null
        },
        
        resourceBundle: function(locale, msg) {
            var resourceBundle = {
                'en' : {
                    "title" : "Reservation System",
                    "today" : "Today",
                    "computer" : "Computer",
                    "mon" : "Monday",
                    "tue" : "Tuesday",
                    "wed" : "Wednesday",
                    "thu" : "Thursday",
                    "fri" : "Friday",
                    "sat" : "Saturday",
                    "sun" : "Sunday",
                    "goto" : "Go to..",
                    "partialRes" : "Partially reserved"
                },
                'cs' : {
                    "title" : "Rezerva\u010dní systém",
                    "today" : "Dnes",
                    "computer" : "Po\u010dítač",
                    "mon" : "Pondelí",
                    "tue" : "Úterý",
                    "wed" : "St\u0159eda",
                    "thu" : "\u010ctvrtek",
                    "fri" : "Pátek",
                    "sat" : "Sobota",
                    "sun" : "Ned\u011ble",
                    "goto" : "Jdi na..",
                    "partialRes" : "\u010cástečne rezervováno"
                },
                'sk' : {
                    "title" : "Rezerva\u010dný systém",
                    "today" : "Dnes",
                    "computer" : "Po\u010dítač",
                    "mon" : "Pondelok",
                    "tue" : "Utorok",
                    "wed" : "Streda",
                    "thu" : "\u0160tvrtok",
                    "fri" : "Piatok",
                    "sat" : "Sobota",
                    "sun" : "Nede\u013ea",
                    "goto" : "Ís\u0165 na..",
                    "partialRes" : "\u010ciastočne rezervované"
                }
                
            }
            return resourceBundle[locale][msg];
        },
        
        mondayDate: function(curDate) {
            if (this.options.daysToDisplay != 7) return curDate;
            var selectedDateDay = curDate.getDay();
            var backDays = selectedDateDay-1;
            if (backDays == -1) {
                backDays = 6;
            }
            var mondayDate = new Date(curDate.getFullYear(), curDate.getMonth(), (curDate.getDate()-backDays));
            
            return mondayDate;
        },
        
        // Set up the widget
        _create: function() {
            var self = this;
            //alert("create");
            self._renderCalendar();
            document.cookie="calDate" + "=" + this.options.date; //insert since date into cookies
        },
        
        _init: function() {
            //alert("init");
            var self = this;
        //            self._renderCalendar();
        //            document.cookie="calDate" + "=" + this.options.date; 
        //            
        },
        
        _renderCalendar: function() {
            var $calendarContainer;
            var self = this;
            var options = this.options;
            
            $('#calendar').find('*').remove();  // when re-rendering, remove the previous instance of calendar
           
            $calendarContainer = $('<div class=\"ui-widget container\">').appendTo(self.element);

            //render the different parts
            // navigation
            
            self._renderCalendarButtons($calendarContainer);
            // header
            if (options.hours != "null") {
                self._renderCalendarHeaderHours($calendarContainer);
            } else {
                self._renderCalendarHeader($calendarContainer);
            }
            // body + get data from json
            var ajaxReq = $.ajax({
                data: {
                    "query":options.query, 
                    "userId":options.userId,
                    "computerId":options.computerId
                },
                url: "JSONServlet",
                async: false,
                dataType: "text"
            });
            ajaxReq.done(function(data) {
                reservations = $.parseJSON(data);
                if (options.hours != "null") {
                    self._renderCalendarBodyHours($calendarContainer,reservations);
                } else {
                    self._renderCalendarBody($calendarContainer,reservations);
                }
            });
        //var d5 = new Date();
        //            alert("renderButtons:" + (d2.getTime() - d1.getTime()));
        //            alert("renderHeader:" + (d3.getTime() - d2.getTime()));
        //            alert("JSON:" + (d4.getTime() - d3.getTime()));
        //            alert("renderBody:" + (d5.getTime() - d4.getTime()));
        },
        
        /**
         * render the navigation buttons + calendar title
         */
        _renderCalendarButtons: function($calendarContainer) {
            var self = this;
            var options = this.options;
            var calendarNavHtml = '';
            calendarNavHtml += '<div class=\"ui-widget-header toolbar\">';
            calendarNavHtml += '<div class=\"nav\">';
            calendarNavHtml += '<button class=\"prev\">Prev</button>';
            calendarNavHtml += '<button class=\"today\">'+this.resourceBundle(this.options.locale,'today')+'</button>';
            calendarNavHtml += '<button class=\"goto\">'+this.resourceBundle(this.options.locale,'goto')+'</button>';
            calendarNavHtml += '<input type=\"hidden\" class=\"datepicker\"/>';
            calendarNavHtml += '<button class=\"next\">Next</button>';
            calendarNavHtml += '</div>';
            calendarNavHtml += '<h1 class=\"caltitle\">'+ this.resourceBundle(this.options.locale,'title')+'</h1>';
            calendarNavHtml += '</div>';

            $(calendarNavHtml).appendTo($calendarContainer);

            $calendarContainer.find('.nav .today')
            .button({
                icons: {
                    primary: 'ui-icon-home'
                }
            })
            .click(function() {
                self.today();
                refreshSelected();
                return false;
            });
            
            $calendarContainer.find('.nav .goto')
            .button({
                icons: {
                    primary: 'ui-icon-triangle-1-e'
                }
            });

            $calendarContainer.find('.nav .prev')
            .button({
                text: false,
                icons: {
                    primary: 'ui-icon-seek-prev'
                }
            })
            .click(function() {
                self.element.resCal('prev');
                refreshSelected();
                return false;
            });

            $calendarContainer.find('.nav .next')
            .button({
                text: false,
                icons: {
                    primary: 'ui-icon-seek-next'
                }
            })
            .click(function() {
                self.element.resCal('next');
                refreshSelected();
                return false;
            });

    
        },
        
        /**
         * render the calendar header
         */
        _renderCalendarHeader: function($calendarContainer) {
            var self = this, options = this.options,
            rowspan = '', colspan = '', calendarHeaderHtml;
            
            //first row
            calendarHeaderHtml = '<div class=\"ui-widget-content header\">';
            calendarHeaderHtml += '<table><tbody><tr>';
            //calendarHeaderHtml += '<td class=\"id-column-header\"' + colspan + '>Id</td>';
            calendarHeaderHtml += '<td class=\"day-column-header day-' + i + '\"' + colspan + '><div class=\"cellwidth\">'+this.resourceBundle(this.options.locale,'computer')+'</div></td>';
            var temp = -1;
            var tempDate = self.mondayDate(new Date(options.date.getTime()));
            for (var i = 0; i < options.daysToDisplay; i++) {
                var d = tempDate.getDate();
                var m = tempDate.getMonth()+1;
                var y = tempDate.getFullYear();
                calendarHeaderHtml += '<td class=\"day-column-header wc-day-' + i + '\"' + colspan + '><div class=\"cellwidth\">';
                var localeDay = "";
                switch (tempDate.getDay()) {
                    case 1:
                        localeDay = this.resourceBundle(this.options.locale,'mon');
                        break;
                    case 2:
                        localeDay = this.resourceBundle(this.options.locale,'tue');
                        break;
                    case 3:
                        localeDay = this.resourceBundle(this.options.locale,'wed');
                        break;
                    case 4:
                        localeDay = this.resourceBundle(this.options.locale,'thu');
                        break;
                    case 5:
                        localeDay = this.resourceBundle(this.options.locale,'fri');
                        break;
                    case 6:
                        localeDay = this.resourceBundle(this.options.locale,'sat');
                        break;
                    case 0:
                        localeDay = this.resourceBundle(this.options.locale,'sun');
                        break;
                    
                }
                calendarHeaderHtml += localeDay + '<br/>';
                if (this.options.locale == 'en') {
                    calendarHeaderHtml += m+"/"+d+"/"+y+'</div></td>';
                }
                else {
                    calendarHeaderHtml += d+'.'+m+'.'+y+'</div></td>';
                }
                
                tempDate.setDate(tempDate.getDate() + 1);
            }
            
            //close the header
            calendarHeaderHtml += '</tbody></table></div>';
            
            $(calendarHeaderHtml).appendTo($calendarContainer);
        },

        _renderCalendarHeaderHours: function($calendarContainer) {
            var self = this, options = this.options,
            rowspan = '', colspan = '', calendarHeaderHtml;
            
            //first row
            calendarHeaderHtml = '<div class=\"ui-widget-content header\">';
            calendarHeaderHtml += '<table><tbody><tr>';
            //calendarHeaderHtml += '<td class=\"id-column-header\"' + colspan + '>Id</td>';
            calendarHeaderHtml += '<td class=\"day-column-header day-' + i + '\"' + colspan + '><div class=\"cellwidth\">'+this.resourceBundle(this.options.locale,'computer')+'</div></td>';
            var temp = -1;
            
            var tempDate = new Date(options.date.getTime());
            tempDate.setHours(options.date.getHours(), 0, 0, 0);
            for (var i = 0; i < options.daysToDisplay; i++) {
                calendarHeaderHtml += '<td class=\"day-column-header wc-day-' + i + '\"' + colspan + '><div class=\"cellwidth\">';
                if (this.options.locale == 'en') {
                    calendarHeaderHtml += (tempDate.getMonth()+1) + '/' + tempDate.getDate() + "/" + tempDate.getFullYear()+'<br/>';
                    var hr = tempDate.getHours();
                    var convHrs = "";
                    var ampmSwitch = "";
                    ampmSwitch = (hr > 12)? "PM":"AM"; 
                    convHrs = (hr >12)? hr-12:hr;
                    calendarHeaderHtml += convHrs + ":" + tempDate.getMinutes() + "0 " + ampmSwitch + '</div></td>';
                }
                else {
                    calendarHeaderHtml += tempDate.getDate() + '.' + (tempDate.getMonth()+1) + "." + tempDate.getFullYear()+'<br/>';
                    calendarHeaderHtml += tempDate.getHours()+':'+tempDate.getMinutes()+'0</div></td>';            
                    
                }
                tempDate.setHours(tempDate.getHours() + 1);
            }
                        
            //close the header
            calendarHeaderHtml += '</tbody></table></div>';
                        
            $(calendarHeaderHtml).appendTo($calendarContainer);
            
            
        },

        /**
         * render the calendar body.
         */
        _renderCalendarBody: function($calendarContainer, data) {            
            var self = this, 
            options = this.options,
            
            $calendarBody, $calendarTableTbody;
            $calendarBody = '<div class=\"calbody\">';
            $calendarBody += '<table id=\"caltable\" border="1">';
            $calendarBody += '<tbody>';  
            if (data != null) {
                for (var j = 0; j < data.length; j++) { //j = number of lines (computers)
                    if ((j+1) % 2 == 0) {
                        $calendarBody += '<tr class="odd">';
                    }
                    else {
                        $calendarBody += '<tr class="even">';
                    }
                    //$calendarBody += '<td class=\"id-column-body\">' + data[j].id + '</td>'; 
                    $calendarBody += '<td class=\"day-column-body\" id=\"'+data[j].id+'\"><div class=\"cellwidth\"><b>' + data[j].name + '</b></div></td>';
                    var compReservations = data[j].reservations;
                    
                    var thisDate = self.mondayDate(new Date(options.date.getTime()));
                    for(var k = 0; k < options.daysToDisplay; k++) { // for each day
                        var reserved = false;
                        var dateString = (thisDate.getDate()) + "." + (thisDate.getMonth()+1) + "." + thisDate.getFullYear();
                        
                        if (compReservations != null) { // if this computers has some reservations
                            for (var q = 0; q < compReservations.length; q++) { // for each reservation check if current day should be marked as reserved
                               
                                var stringDate = thisDate.getFullYear() + "-";
                                if ((thisDate.getMonth()+1) < 10) {
                                    stringDate += "0";                              // gets the desired format of date
                                }
                                stringDate += (thisDate.getMonth()+1) + "-";
                                if (thisDate.getDate() < 10) {
                                    stringDate += "0";
                                }
                                stringDate += thisDate.getDate();
                                var tempDate = new Date(stringDate).getTime();
                                var test1 = compReservations[q].since;

                                var test2 = compReservations[q].until;

                                new Date(Date.parse("08/11/2012","MM/dd/yyyy"));
                                var since = new Date(compReservations[q].since).getTime();
                                var until = new Date(compReservations[q].until).getTime();
                                var tempDateEnd = new Date(tempDate);
                                tempDate -= 3600000;
                                tempDateEnd.setHours(23, 59, 59);
                                
                                if (tempDate >= since && tempDateEnd <= until)
                                    if ((tempDate >= since) && (tempDateEnd <= until)) {
                                        if (compReservations[q].user == options.userName) {

                                            $calendarBody += '<td class="myreserved"><div class=\"cellwidth\">';
                                        }
                                        else {

                                            $calendarBody += '<td class="reserved"><div class=\"cellwidth\">';
                                        }
                                        $calendarBody += compReservations[q].user+'</div></td>';
                                        reserved = true;
                                        break;
                                    }
                                    
                                if ((since >= tempDate && since <= tempDateEnd) || (until >= tempDate && until <= tempDateEnd)) {
                                    $calendarBody += '<td class="partialreserved"><div class=\"cellwidth\">' + this.resourceBundle(this.options.locale,'partialRes');
                                    $calendarBody += '</div></td>';
                                    reserved = true;

                                    break;
                                }
                            }
                        }     
                        if (reserved) {
                            thisDate.setDate(thisDate.getDate()+1);
                            continue;
                        }
                        var compDate = new Date();
                        compDate.setHours(0);
                        compDate.setMinutes(0);
                        compDate.setSeconds(0);
                        compDate.setMilliseconds(0);
                        var idString;
                        if (thisDate < compDate) { 
                            idString = data[j].id+";"+thisDate.getTime()+";"+data[j].compName;
                            $calendarBody += '<td class="freePast" id=\"'+idString+'\"><div class=\"cellwidth\"></div></td>';

                        }
                        else {
                            idString = data[j].id+";"+thisDate.getTime()+";"+data[j].compName;
                            $calendarBody += '<td class="free" id=\"'+idString+'\"><div class=\"cellwidth\"></div></td>';

                        }
                        thisDate.setDate(thisDate.getDate()+1);
                    }
                    $calendarBody += '</tr>';
                }
            }
            $calendarBody += '</tbody>';
            $calendarBody += '</table>';
            $calendarBody += '</div>';
            $calendarBody = $($calendarBody);
            $calendarBody.appendTo($calendarContainer);
        },
        
        _renderCalendarBodyHours: function($calendarContainer, data) {            
            var self = this, 
            options = this.options,
            
            $calendarBody, $calendarTableTbody;
            
            $calendarBody = '<div class=\"calbody\">';
            $calendarBody += '<table id=\"caltable\" border="1">';
            $calendarBody += '<tbody>';  
            if (data != null) {
                for (var j = 0; j < data.length; j++) { //j = number of lines (computers)
                    if ((j+1) % 2 == 0) {
                        $calendarBody += '<tr class="odd">';
                    }
                    else {
                        $calendarBody += '<tr class="even">';
                    }
                    //$calendarBody += '<td class=\"id-column-body\">' + data[j].id + '</td>'; 
                    $calendarBody += '<td class=\"day-column-body\" id=\"'+data[j].id+'\"><div class=\"cellwidth\"><b>' + data[j].name + '</b></div></td>';
                    var compReservations = data[j].reservations;
                    var thisDate = new Date(options.date.getTime());
                    
                    thisDate.setHours(options.date.getHours(), 0, 0, 0);
                    for(var k = 0; k < options.daysToDisplay; k++) { // for each day
                        var reserved = false;
                        var dateString = (thisDate.getDate()) + "." + (thisDate.getMonth()+1) + "." + thisDate.getFullYear();
                        if (compReservations != null) { // if this computers has some reservations
                            for (var q = 0; q < compReservations.length; q++) { // for each reservation check if current day should be marked as reserved
//                                var stringDate = thisDate.getFullYear() + "-";
//                                if ((thisDate.getMonth()+1) < 10) {
//                                    stringDate += "0";                              // gets the desired format of date
//                                }
//                                stringDate += (thisDate.getMonth()+1) + "-";
//                                if (thisDate.getDate() < 10) {
//                                    stringDate += "0";
//                                }
//                                stringDate += thisDate.getDate() + " ";
//                                stringDate += thisDate.getHours() + ":" + thisDate.getMinutes() + ":" + thisDate.getSeconds();
                                var millis = thisDate.getTime();
                                var tempDate = new Date(millis);
                                
                                var since = new Date(compReservations[q].since).getTime();
                                var until = new Date(compReservations[q].until).getTime();
                                var compDate = new Date();
                                var idString = data[j].id+";"+thisDate.getTime()+";"+data[j].compName;
                                if ((tempDate >= since) && (tempDate <= until)) {
                                    
                                    if (compReservations[q].user == options.userName) {
                                        if (thisDate < compDate) {
                                            $calendarBody += '<td class="myreservedPast"><div class=\"cellwidth\">';
                                        }
                                        else {
                                            $calendarBody += '<td class="myreserved" id="'+idString+'"><div class=\"cellwidth\">';
                                        }
                                    }
                                    else {
                                        $calendarBody += '<td class="reserved"><div class=\"cellwidth\">';
                                    }
                                    $calendarBody += compReservations[q].user+'</div></td>';
                                    reserved = true;
                                    break;
                                }
                            }
                        }     
                        if (reserved) {
                            thisDate.setHours(thisDate.getHours()+1);
                            continue;
                        }
                        var compDate = new Date();
                        compDate.setHours(compDate.getHours()-1);
                        
                        if (thisDate < compDate) { 
                            idString = data[j].id+";"+thisDate.getTime()+";"+data[j].compName;
                            $calendarBody += '<td class="freePast" id=\"'+idString+'\"><div class=\"cellwidth\"></div></td>';
                        }
                        else {
                            idString = data[j].id+";"+thisDate.getTime()+";"+data[j].compName;
                            $calendarBody += '<td class="free" id=\"'+idString+'\"><div class=\"cellwidth\"></div></td>';
                        }
                        thisDate.setHours(thisDate.getHours()+1);
                    }
                    $calendarBody += '</tr>';
                }
            }
            $calendarBody += '</tbody>';
            $calendarBody += '</table>';
            $calendarBody += '</div>';
            $calendarBody = $($calendarBody);
            $calendarBody.appendTo($calendarContainer);
        },
        
        // Use the _setOption method to respond to changes to options
        _setOption: function( key, value ) {
            switch( key ) {
                case "date":
                    $.Widget.prototype._setOption.apply( this, arguments );
                    document.cookie="calDate" + "=" + this.options.date;
                    this._renderCalendar();
                    break;
                case "locale":
                    $.Widget.prototype._setOption.apply( this, arguments );
                    //this._renderCalendar();                    
                    break;
                case "userName":
                    $.Widget.prototype._setOption.apply( this, arguments );
                    //this._renderCalendar();
                    break;
                case "daysToDisplay":
                    $.Widget.prototype._setOption.apply( this, arguments );
                    //this._renderCalendar();
                    break;
            }
        },
 
        // Use the destroy method to clean up any modifications your widget has made to the DOM
        destroy: function() {
        // In jQuery UI 1.8, you must invoke the destroy method from the base widget
        },
        
        /*
         * Go to this week
         */
        today: function() {
            var options = this.options;
            var newDate = new Date();
            this._setOption("date", newDate);
        },

        /*
         * Go to next week
         */
        next: function() {
            var options = this.options;
            var newDate = new Date(options.date.getTime());
            if (options.hours != "null") {
                newDate.setHours(newDate.getHours() + options.daysToDisplay);
            }
            else {
                
                newDate.setDate(newDate.getDate() + options.daysToDisplay);
            }
            this._setOption("date", newDate);
        },

        /*
         * Go to previous week
         */
        prev: function() {
            var options = this.options;
            var newDate = new Date(options.date.getTime());
            if (options.hours != "null") {
                newDate.setHours(newDate.getHours() - options.daysToDisplay);
            }
            else {
                newDate.setDate(newDate.getDate() - options.daysToDisplay);
            }
            this._setOption("date", newDate); 
        }
    });
}( jQuery ) );