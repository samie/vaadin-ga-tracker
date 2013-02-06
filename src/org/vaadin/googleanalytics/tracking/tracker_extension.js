window.org_vaadin_googleanalytics_tracking_GoogleAnalyticsTracker = function() {
	var self = this;
	this.registerRpc({
		track : function(pageId) {
			var state = self.getState();
			var trackerId = state.trackerId;
			var domainName = state.domainName;

			try {

				var pageTracker = window._gat._getTracker(trackerId);
				console.log("Using tracker id " + trackerId);
				if (domainName) {
					console.log("Tracking domain " + domainName);
					pageTracker._setDomainName(domainName);
				}
				if (pageId) {
					pageTracker._trackPageview(pageId);
					console.log("Tracked page " + pageId);
				} else {
					pageTracker._trackPageview();
					console.log("Tracked unknown page");
				}
			} catch (err) {
				window.alert(err);
			}

		}
	});
}
