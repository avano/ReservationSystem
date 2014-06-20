function getLocaleFromCookie() {
    var re = new RegExp("remember_locale=([^;]+)");
    var localeFromCookie = re.exec(document.cookie);
    if (localeFromCookie != null) {
        return localeFromCookie[1]
    }
    else return "en";
}

function getDateFromCookie() {
    re = new RegExp("calDate=([^;]+)");
    var dateFromCookie = re.exec(document.cookie); // get date from cookie
    var returnDate;
    if (dateFromCookie != null) {
        returnDate = new Date(dateFromCookie[1]);
    }
    else {
        returnDate = new Date();
    }
    return returnDate;
}

function bubbleSortId(resArray) {
    if (resArray.length < 2) {
        return resArray;
    }
    var reservations = resArray;
    var pass = 1;
    do {
        var i = 0;
        do {
            var array1 = reservations[i].split(";");
            var array2 = reservations[i+1].split(";");
            if (parseInt(array1[0]) > parseInt(array2[0])) {
                var temp = resArray[i];
                resArray[i] = resArray[i+1];
                resArray[i+1] = temp;
            }
                        
            i++;
        } while(i != reservations.length - pass);
        pass++;
    } while(pass != reservations.length);
    return resArray;
}
            
function bubbleSort(resArray) {
    if (resArray.length < 2) {
        return resArray;
    }
    bubbleSortId(resArray);
    var reservations = resArray;
    var pass = 1;
    do {
        var i = 0;
        do {
            var array1 = reservations[i].split(";");
            var array2 = reservations[i+1].split(";");
            if (parseInt(array1[1]) > parseInt(array2[1])) {
                if (parseInt(array1[0]) == parseInt(array2[0])) {
                    var temp = resArray[i];
                    resArray[i] = resArray[i+1];
                    resArray[i+1] = temp;
                }
            }
            i++;
        } while(i != reservations.length - pass);
        pass++;
    } while(pass != reservations.length);
    return resArray;
}

function addElement(cols,element) {
    if (!contains(cols,element)) {
        cols.push(element);
    }
}
            
function contains(a, obj) {
    var i = a.length;
    while (i--) {
        if (a[i] === obj) {
            return true;
        }
    }
    return false;
}
            
function removeElement(cols,element) {
    for(var i = cols.length-1; i >= 0; i--){  // STEP 1
        if(cols[i] == element){              // STEP 2
            cols.splice(i,1);                 // STEP 3
        }
    }
}
            
function removeAll(cols) {
    cols.splice(0,cols.length);
}
            
function getElements(cols) {
    var k = 0;
    var ret = "";
    var retArray = new Array();
    while (true) {
        if (k==cols.length) break;
        retArray.push(cols[k]);                  
        ret += cols[k] + ",";
        k++;
    }
    return retArray;
}
            
function hasElements(cols) {
    return cols.length != 0;
}