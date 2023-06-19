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

    this.ga4Load = function() {
        var state = self.getState();
		var trackerId = state.trackerId;

		(function() {
			var ga4 = document.createElement('script');
			ga4.type = 'text/javascript';
			ga4.async = true;
			ga4.src = 'https://www.googletagmanager.com/gtag/js?id=' + trackerId;
			var s = document.getElementsByTagName('script')[0];
			s.parentNode.insertBefore(ga4, s);
		})();

    };

    this.legacyInit = function() {

        var state = self.getState();
        var trackerId = state.trackerId;
        var domainName = state.domainName;
        var allowAnchor = state.allowAnchor;
        var allowLinker = state.allowLinker;

        window._gaq.push(['_setAccount', trackerId]);

        if (domainName) {
            window._gaq.push(['_setDomainName', domainName]);
        }
        window._gaq.push(['_setAllowAnchor', allowAnchor]);
        window._gaq.push(['_setAllowLinker', allowLinker]);
    };

    function gtag() {
        window.dataLayer.push(arguments);
	}

    this.ga4Init = function() {
        var state = self.getState();
		var trackerId = state.trackerId;
		window.dataLayer = window.dataLayer || [];
		gtag('js', new Date());
		gtag('config', trackerId);
    };


    this.trackPageView = function(pageId) {
        if (self.getState().universalTracking) {
            self.ga4Track(pageId);
        } else {
            self.legacyTrack(pageId);
        }
    };
    
    this.trackEvent = function (eventCategory, eventAction, eventLabel, eventValue) {
        gtag('event', eventAction, {
               'event_category': eventCategory,
               'event_label': eventLabel,
               'value': eventValue
        });
    };
    
    this.legacyTrack = function(pageId) {

        if (pageId) {
            window._gaq.push(['_trackPageview', pageId]);
        } else {
            window._gaq.push(['_trackPageview']);
        }
    };

    this.ga4Track = function(pageId) {
        if (pageId) {
            gtag('event', 'page_view', {
				'page_title': pageId
			});
        } else {
            gtag('event', 'page_view');
        }
    };

    this.onStateChange = function() {

        // Load if not loaded yet
        if (!self.apiLoaded) {
            if (self.getState().universalTracking) {
                self.ga4Load();
            } else {
                self.legacyLoad();
            }
            self.apiLoaded = true;
        }

        // Init tracking
        if (self.getState().universalTracking) {
            self.ga4Init();
        } else {
            self.legacyInit();
        }


    };

};
