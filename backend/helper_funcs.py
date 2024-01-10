from math import radians, sin, cos, sqrt, atan2

def haversine_distance(coord1, coord2):
    # Radius of the Earth in kilometers
    R = 6371.0

    # Extract latitude and longitude from coordinates
    lat1, lon1 = radians(coord1[0]), radians(coord1[1])
    lat2, lon2 = radians(coord2[0]), radians(coord2[1])

    # Differences in coordinates
    dlat = lat2 - lat1
    dlon = lon2 - lon1

    # Haversine formula
    a = sin(dlat / 2)**2 + cos(lat1) * cos(lat2) * sin(dlon / 2)**2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    # Calculate the distance(km)
    distance = R * c * 1000

    return distance

# Example usage
coord1 = (37.7749, -122.4194)  # San Francisco, CA
coord2 = (34.0522, -118.2437)  # Los Angeles, CA
