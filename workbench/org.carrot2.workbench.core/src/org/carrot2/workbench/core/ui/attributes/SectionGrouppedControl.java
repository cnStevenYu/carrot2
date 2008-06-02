package org.carrot2.workbench.core.ui.attributes;

import static org.eclipse.swt.SWT.DEFAULT;
import static org.eclipse.swt.SWT.NONE;

import java.util.*;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.workbench.editors.AttributeChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

public class SectionGrouppedControl implements IAttributesGrouppedControl
{

    private Composite mainControl;
    private java.util.List<AttributesPage> pages = new ArrayList<AttributesPage>();
    private BindableDescriptor descriptor;

    public void init(BindableDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    public void createGroup(Object label)
    {
        createGroup(label.toString(), descriptor.attributeGroups.get(label));
    }

    public void createOthers()
    {
        createGroup("Other", descriptor.attributeDescriptors);
    }

    @SuppressWarnings("unchecked")
    private void createGroup(String text, Map<String, AttributeDescriptor> attributes)
    {
        final Section group =
            new Section(mainControl, ExpandableComposite.TWISTIE
                | ExpandableComposite.CLIENT_INDENT);
        group.setText(text);
        group.setSeparatorControl(new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL));
        AttributesPage page =
            new AttributesPage((Class<? extends ProcessingComponent>) descriptor.type,
                attributes);

        Composite inner = new Composite(group, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginBottom = 10;
        inner.setLayout(layout);
        page.createControl(inner);
        page.getControl().setLayoutData(
            new GridData(GridData.FILL, GridData.BEGINNING, true, false));

        group.setClient(inner);
        group.setExpanded(true);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false);
        gd.heightHint = group.computeSize(DEFAULT, DEFAULT).y;
        group.setLayoutData(gd);

        mainControl.setSize(mainControl.computeSize(DEFAULT, DEFAULT));
        group.addExpansionListener(new ExpansionAdapter()
        {
            @Override
            public void expansionStateChanged(ExpansionEvent e)
            {
                mainControl.setSize(mainControl.computeSize(DEFAULT, DEFAULT));
                mainControl.layout();
            }
        });

        pages.add(page);
    }

    public void createMainControl(Composite parent)
    {
        ScrolledComposite scroll =
            new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroll.setLayout(new FillLayout());
        mainControl = new Composite(scroll, NONE);
        scroll.setContent(mainControl);
        mainControl.setLayout(new GridLayout());
    }

    public void dispose()
    {
        for (AttributesPage page : pages)
        {
            page.dispose();
        }
    }

    public Composite getControl()
    {
        if (!mainControl.isDisposed())
        {
            return mainControl.getParent();
        }
        else
        {
            return null;
        }
    }

    public List<AttributesPage> getPages()
    {
        return pages;
    }

    public void addAttributeChangeListener(AttributeChangeListener listener)
    {
        for (AttributesPage page : pages)
        {
            page.addAttributeChangeListener(listener);
        }
    }

    public void removeAttributeChangeListener(AttributeChangeListener listener)
    {
        for (AttributesPage page : pages)
        {
            page.removeAttributeChangeListener(listener);
        }
    }

    public void setAttributeValue(String key, Object value)
    {
        for (AttributesPage page : pages)
        {
            if (page.setAttributeValue(key, value))
            {
                break;
            }
        }
    }

}
