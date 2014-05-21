window.org_vaadin_googleanalytics_tracking_GoogleAnalyticsTracker = function() {

    var self = this;
    this.apiLoaded = false;

    this.legacyLoad = function() {

        window._gaq = window._gaq || [];

        (function() {
            var ga = document.createElement('script');
            ga.type = 'text/javascript';
            ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0];
            s.parentNode.insertBefore(ga, s);
        })();

    };

    this.universalLoad = function() {
        (function(i, s, o, g, r, a, m) {
            i['GoogleAnalyticsObject'] = r;
            i[r] = i[r] || function() {
                (i[r].q = i[r].q || []).push(arguments)
            }, i[r].l = 1 * new Date();
            a = s.createElement(o),
                    m = s.getElementsByTagName(o)[0];
            a.async = 1;
            a.src = g;
            m.parentNode.insertBefore(a, m)
        })(window, document, 'script', '//www.google-analytics.com/analytics.js', '_gaut');

    };

    this.legacyInit = function() {

        var state = self.getState();
        var trackerId = state.trackerId;
        var domainName = state.domainName;
        var allowAnchor = state.allowAnchor;


        window._gaq.push(['_setAccount', trackerId]);

        if (domainName) {
            window._gaq.push(['_setDomainName', domainName]);
        }
        window._gaq.push(['_setAllowAnchor', allowAnchor]);
    };



    this.universalInit = function() {

        var state = self.getState();
        var trackerId = state.trackerId;
        var domainName = state.domainName;
        var allowAnchor = state.allowAnchor;

        window._gaut('create', trackerId, {'cookieDomain': domainName, 'allowAnchor': allowAnchor});
    };


    this.track = function(pageId) {
        if (self.getState().universalTracking) {
            self.universalTrack(pageId);
        } else {
            self.legacyTrack(pageId);
        }
    };

    this.legacyTrack = function(pageId) {

        if (pageId) {
            window._gaq.push(['_trackPageview', pageId]);
        } else {
            window._gaq.push(['_trackPageview']);
        }
    };

    this.universalTrack = function(pageId) {
        if (pageId) {
            window._gaut('send', 'pageview', {
                'page': pageId});
        } else {
            window._gaut('send', 'pageview');
        }
    };

    this.onStateChange = function() {

        // Load if not loaded yet
        if (!self.apiLoaded) {
            if (self.getState().universalTracking) {
                self.universalLoad();
            } else {
                self.legacyLoad();
            }
            self.apiLoaded = true;
        }

        // Init tracking
        if (self.getState().universalTracking) {
            self.universalInit();
        } else {
            self.legacyInit();
        }


    };

};
