window.org_vaadin_googleanalytics_tracking_GoogleAnalyticsTracker = function() {

	this.track = function(pageId) {
		var state = this.getState();
		var trackerId = state.trackerId;
		var domainName = state.domainName;
		var allowAnchor = state.allowAnchor;
		var pageTracker = window._gat._getTracker(trackerId);
		if (domainName) {
			pageTracker._setDomainName(domainName);
		}

		pageTracker._setAllowAnchor(allowAnchor);


		if (pageId) {
			pageTracker._trackPageview(pageId);
		} else {
			pageTracker._trackPageview();
		}

	};
};
