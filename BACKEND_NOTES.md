# Back-end development notes

## Yelp API query

### Process

1. Construct and send an OAuth 1.0a request via the Yelp API service
using front-end data (location & user preferences).
    a. Construct the Yelp API service with:
        i. Consumer key
        ii. Consumer secret
        iii. Token key
        iv. Token secret
    b. Form an OAuth request which is a URL of the form:
    
        http://api.yelp.com/v2/search?...
    
    i. Parameters: We need at minimum a location, though practically we
    would always want a minimum category filter of "food" (includes
    restaurants, food trucks, etc.)
        * location: needs to have at least one of city, state, or ZIP. 
        * category_filter: "food" should probably be always sufficient
        * term: search term(s) within food
        * cll: latitude & longitude
        * limit: how many search results to return
    ii. Use device location: We must determine at least a city or ZIP
    from the device's location *and* include the CLL based on device
    location. For example:
        
            http://api.yelp.com/v2/search/?term=tacos&location=94127&limit=10&category_filter=food&cll=37.7412617,-122.4649631
        
    iii. *or* User specifies location: Use location data specified by user.
    For example:
        
            http://api.yelp.com/v2/search/?term=tacos&location=1600 Holloway Avenue, San Francisco, CA 94132&limit=10&category_filter=food        
        
    CLL would be unnecessary in this case.
            

2. Receive a JSON-encoded String response and parse it, making the resultant
data available to the front-end.
