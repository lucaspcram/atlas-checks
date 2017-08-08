package org.openstreetmap.atlas.checks.base;

import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.atlas.checks.base.checks.BaseTestCheck;
import org.openstreetmap.atlas.checks.configuration.ConfigurationResolver;
import org.openstreetmap.atlas.utilities.configuration.Configuration;

/**
 * Test the countries.whitelist and countries.blacklist functionality. This allows users to filter
 * checks based on countries, by either allowing only countries in the whitelist or not allowing any
 * countries in the blacklist
 * 
 * @author cuthbertm
 */
public class CountryTest
{
    /**
     * Test that if you supply an empty country whitelist, that the option is essentially ignored
     * and all countries are allowed.
     */
    @Test
    public void testNoCountries()
    {
        final String countryConfig = "{\"BaseTestCheck\":{\"countries.whitelist\":[]}}";
        this.testConfiguration(countryConfig, "AIA", true);
    }

    /**
     * Test that only countries included in the whitelist are used, and any countries not included
     * in the whitelist are ignored.
     */
    @Test
    public void testWhitelistCountries()
    {
        final String countryConfig = "{\"BaseTestCheck\":{\"countries.whitelist\":[\"AIA\",\"DOM\"]}}";
        this.testConfiguration(countryConfig, "AIA", true);
        this.testConfiguration(countryConfig, "DOM", true);
        this.testConfiguration(countryConfig, "IRN", false);
    }

    /**
     * Test that all countries included in the blacklist are ignored, and any countries not included
     * in the blacklist are used.
     */
    @Test
    public void testBlacklistCountries()
    {
        final String countryConfig = "{\"BaseTestCheck\":{\"countries.blacklist\":[\"AIA\",\"DOM\"]}}";
        this.testConfiguration(countryConfig, "AIA", false);
        this.testConfiguration(countryConfig, "DOM", false);
        this.testConfiguration(countryConfig, "IRN", true);
    }

    /**
     * Two tests: 1. Test that if containing whitelist and blacklist, that only countries in the
     * whitelist are used and all countries in the blacklist are ignored. 2. Test that if a country
     * is both in the whitelist and the blacklist that the country value in the whitelist takes
     * precedence
     */
    @Test
    public void testCombinationCountries()
    {
        final String countryConfig = "{\"BaseTestCheck\":{\"countries.whitelist\":[\"IRN\",\"IRQ\"],\"countries.blacklist\":[\"AIA\",\"DOM\"]}}";
        this.testConfiguration(countryConfig, "IRN", true);
        this.testConfiguration(countryConfig, "IRQ", true);
        this.testConfiguration(countryConfig, "AIA", false);
        this.testConfiguration(countryConfig, "DOM", false);
        final String countryConfig2 = "{\"BaseTestCheck\":{\"countries.whitelist\":[\"IRN\"],\"countries.blacklist\":[\"IRN\"]}}";
        this.testConfiguration(countryConfig2, "IRN", true);
    }

    /**
     * Test to make sure both the keys "countries" and "countries.whitelist" work for the whitelist
     * option
     */
    @Test
    public void testBackwardsCompatibility()
    {
        final String countryConfig = "{\"BaseTestCheck\":{\"countries\":[]}}";
        this.testConfiguration(countryConfig, "AIA", true);
    }

    /**
     * Private function that does the check for all the unit tests
     * 
     * @param config
     *            A stringified version of the configuration that will be resolved using the
     *            {@link ConfigurationResolver}
     * @param testCountry
     *            The country that is being tested
     * @param test
     *            Whether the country being tested should be included or excluded when running the
     *            checks
     */
    private void testConfiguration(final String config, final String testCountry,
            final boolean test)
    {
        final Configuration configuration = ConfigurationResolver.inlineConfiguration(config);
        final BaseTestCheck testCheck = new BaseTestCheck(configuration);

        // If no countries in the configuration than it is assumed that a flag will be produced. The
        // BaseTestCheck will always produce a flag if everything else gets through
        if (test)
        {
            Assert.assertTrue(testCheck.validCheckForCountry(testCountry));
        }
        else
        {
            Assert.assertFalse(testCheck.validCheckForCountry(testCountry));
        }
    }
}
