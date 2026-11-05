/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OwnerTests {

	@Test
	void addPetAddsPersistedPet() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(5);
		pet.setName("Buddy");

		owner.addPet(pet);

		assertTrue(owner.getPets().contains(pet));
		assertEquals(1, owner.getPets().size());
	}

	@Test
	void addPetDoesNotAddDuplicatePet() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(5);
		pet.setName("Buddy");

		owner.addPet(pet);
		owner.addPet(pet);

		assertEquals(1, owner.getPets().size());
	}

	@Test
	void setTelephoneRemovesCommonSeparators() {
		Owner owner = new Owner();

		owner.setTelephone("608-555-1023");
		assertEquals("6085551023", owner.getTelephone());

		owner.setTelephone("608 555 1023");
		assertEquals("6085551023", owner.getTelephone());

		owner.setTelephone("(608)5551023");
		assertEquals("6085551023", owner.getTelephone());
	}

	@Test
	void separatedTelephoneIsValid() {
		assertTrue(validateTelephone("(608) 555-1023").isEmpty());
	}

	@Test
	void telephoneWithTooFewDigitsIsInvalid() {
		assertTelephoneInvalid("608-555-102");
	}

	@Test
	void telephoneWithLettersIsInvalid() {
		assertTelephoneInvalid("608-555-102a");
	}

	private static Set<ConstraintViolation<Owner>> validateTelephone(String telephone) {
		Owner owner = new Owner();
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone(telephone);

		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		return validator.validate(owner);
	}

	private static void assertTelephoneInvalid(String telephone) {
		Set<ConstraintViolation<Owner>> violations = validateTelephone(telephone);
		assertEquals(1, violations.size());
		ConstraintViolation<Owner> violation = violations.iterator().next();
		assertEquals("telephone", violation.getPropertyPath().toString());
		assertEquals("{telephone.invalid}", violation.getMessageTemplate());
	}

}
