package com.allybros.superego.api.mapper;


import com.allybros.superego.api.response.ProfileResponse;
import com.allybros.superego.unit.User;

public class UserMapper {

    private UserMapper() {}

    /**
     * Converts a ProfileResponse object to a User object, mapping common and specific properties.
     *
     * @param profileResponse The ProfileResponse object to convert.
     * @return A User object with properties populated from the provided ProfileResponse.
     */
    public static User fromProfileResponse(ProfileResponse profileResponse) {
        User user = new User();
        // Common properties
        user.setUserType(profileResponse.getUserType());
        user.setRated(profileResponse.getRated());
        user.setCredit(profileResponse.getCredit());
        user.setUsername(profileResponse.getUsername());
        user.setUserBio(profileResponse.getUserBio());
        user.setTestId(profileResponse.getTestId());
        user.setTestResultId(profileResponse.getTestResultId());
        user.setEmail(profileResponse.getEmail());

        // Specific properties
        user.setImage(profileResponse.getAvatar());
        user.setScores(profileResponse.getScores());
        user.setOcean(profileResponse.getOcean());
        user.setPersonality(profileResponse.getPersonality());
        return user;
    }

}
