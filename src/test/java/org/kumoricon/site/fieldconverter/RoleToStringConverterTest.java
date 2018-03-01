package org.kumoricon.site.fieldconverter;

import com.vaadin.v7.data.util.converter.Converter;
import org.junit.Test;
import org.kumoricon.model.role.Role;

import java.util.Locale;

import static junit.framework.TestCase.assertEquals;

public class RoleToStringConverterTest {
    private RoleToStringConverter converter = new RoleToStringConverter();
    private final Role role;
    private final Role roleWithNullName;

    public RoleToStringConverterTest() {
        roleWithNullName = new Role();
        roleWithNullName.setId(1);

        role = new Role();
        role.setId(2);
        role.setName("Tester");
    }

    @Test(expected=Converter.ConversionException.class)
    public void convertToModel() {
        converter.convertToModel("Test", Role.class, Locale.ENGLISH);
    }

    @Test
    public void convertToPresentation() {
        assertEquals("Role with name", "Tester", converter.convertToPresentation(role, String.class, Locale.ENGLISH));
    }

    @Test
    public void convertToPresentationNullName() {
        assertEquals("Role with null name", "Role ID 1",
                converter.convertToPresentation(roleWithNullName, String.class, Locale.ENGLISH));
    }

    @Test
    public void getModelType() {
        assertEquals(Role.class, converter.getModelType());
    }

    @Test
    public void getPresentationType() {
        assertEquals(String.class, converter.getPresentationType());
    }

}