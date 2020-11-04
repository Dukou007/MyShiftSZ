package com.pax.tms.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;

public class HarmcrestJunitCase extends TestCase {

	public static class Biscuit {
		private String name;
		private int chocolateChipCount = 10;
		private int HazelnutCount = 3;

		public Biscuit() {
		}

		public Biscuit(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getChocolateChipCount() {
			return chocolateChipCount;
		}

		public void setChocolateChipCount(int chocolateChipCount) {
			this.chocolateChipCount = chocolateChipCount;
		}

		public int getHazelnutCount() {
			return HazelnutCount;
		}

		public void setHazelnutCount(int hazelnutCount) {
			HazelnutCount = hazelnutCount;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Biscuit other = (Biscuit) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

	Biscuit theBiscuit = new Biscuit("Ginger");
	Biscuit myBiscuit = new Biscuit("Ginger");

	@Test
	public void testEqualTo1() {
		assertThat(theBiscuit, equalTo(myBiscuit));
	}

	@Test
	public void testEqualTo2() {
		assertThat("reason: chocolate chips", theBiscuit.getChocolateChipCount(), equalTo(10));
		assertThat("reason: hazelnuts", theBiscuit.getHazelnutCount(), equalTo(3));
		assertThat("reason: allof", theBiscuit.getChocolateChipCount(), allOf(lessThan(100), greaterThan(1)));
	}

	@Test
	public void testAllOf() {
		assertThat("reason: chocolate chips", theBiscuit.getChocolateChipCount(), allOf(lessThan(100), greaterThan(1)));
	}

	@Test
	public void testAnyOf() {
		assertThat("reason: chocolate chips", theBiscuit.getChocolateChipCount(), anyOf(equalTo(10), greaterThan(100)));
	}

	@Test
	public void testNot() {
		assertThat("reason: chocolate chips", theBiscuit.getChocolateChipCount(), not(equalTo(50)));
	}

	@Test
	public void testHasToString() {
		assertThat(theBiscuit, hasToString(equalTo("Ginger")));
	}

	@Test
	public void testInstanceOf() {
		assertThat(theBiscuit, instanceOf(Biscuit.class));
	}

	@Test
	public void testNotNullValue() {
		assertThat(theBiscuit, notNullValue());
	}

	@Test
	public void testNullValue() {
		assertThat(theBiscuit, not(nullValue()));
	}

	@Test
	public void testSameInstance() {
		assertThat(theBiscuit, sameInstance(theBiscuit));
	}

	@Test
	public void testHasProperty() {
		assertThat(theBiscuit, hasProperty("name"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testArray() {
		assertThat(new Integer[] { 1, 2, 3 }, is(array(equalTo(1), equalTo(2), equalTo(3))));
	}

	@Test
	public void testHasItemInArray() {
		assertThat(new Integer[] { 1, 2, 3 }, is(hasItemInArray(equalTo(1))));
	}

	Map<String, String> myMap = new HashMap<String, String>();
	{
		myMap.put("bar", "foo");
	}

	@Test
	public void testHasEntry() {
		assertThat(myMap, hasEntry("bar", "foo"));
	}

	@Test
	public void testHasKey() {
		assertThat(myMap, hasKey("bar"));
	}

	@Test
	public void testHasValue() {
		assertThat(myMap, hasValue("foo"));
	}

	@Test
	public void testHasItem() {
		assertThat(Arrays.asList("foo", "bar"), hasItem("bar"));
	}

	@Test
	public void testHasItems() {
		assertThat(Arrays.asList("foo", "bar", "baz"), hasItems("baz", "bar"));
	}

	@Test
	public void testCloseTo() {
		assertThat(1.03, is(closeTo(1.0, 0.04)));
	}

	@Test
	public void testGreaterThan() {
		assertThat(2, greaterThan(1));
	}

	@Test
	public void testGreaterThanOrEqualTo() {
		assertThat(1, greaterThanOrEqualTo(1));
	}

	@Test
	public void testLessThan() {
		assertThat(1, lessThan(2));
	}

	@Test
	public void testLessThanOrEqualTo() {
		assertThat(1, lessThanOrEqualTo(1));
	}

	@Test
	public void testEqualToIgnoringCase() {
		assertThat("Foo", equalToIgnoringCase("FOO"));
	}

	@Test
	public void testEqualToIgnoringWhiteSpace() {
		assertThat("   my\tfoo  bar ", equalToIgnoringWhiteSpace(" my  foo bar"));
	}

	@Test
	public void testContainsString() {
		assertThat("myStringOfNote", containsString("ring"));
	}

	@Test
	public void testEndsWith() {
		assertThat("myStringOfNote", endsWith("Note"));
	}

	@Test
	public void testStartsWith() {
		assertThat("myStringOfNote", startsWith("my"));
	}

	@Test
	public void testIs() {
		assertThat("myStringOfNote", is(startsWith("my")));
	}

	@Test
	public void testDescribedAs() {
		BigDecimal myBigDecimal = new BigDecimal("1000");
		assertThat(new BigDecimal("1000"),
				describedAs("a big decimal equal to %0", equalTo(myBigDecimal), myBigDecimal.toPlainString()));
	}

	@Test
	public void testAnything() {
		assertThat(11, anything("anuthing ignored"));
	}

}
