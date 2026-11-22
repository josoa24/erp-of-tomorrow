import { ChevronDown, Search, X } from 'lucide-react';
import { useId, useMemo, useRef } from 'react';
import { cn } from '@/lib/utils';
import { ComboboxOptionRow } from './combobox-option';
import { ComboboxPopup } from './combobox-popup';
import type { ComboboxProps } from './combobox-types';
import { Field } from './field';
import { fieldControlClass, helperIdFor } from './field-styles';
import { Spinner } from './spinner';
import { useCombobox } from './use-combobox';
import { useComboboxPosition } from './use-combobox-position';
import { useOutsidePointerDown } from './use-outside-pointerdown';

export function Combobox(props: ComboboxProps) {
  const { label, hideLabel, helper, invalid, disabled, unavailableText, loading = false, className } = props;
  const { value = null, placeholder, emptyText = 'Aucun résultat', renderOption } = props;
  const generatedId = useId();
  const id = props.id ?? generatedId;
  const listId = `${id}-listbox`;
  const anchorRef = useRef<HTMLDivElement>(null);
  const popupRef = useRef<HTMLDivElement>(null);
  const box = useCombobox({ ...props, id });
  const refs = useMemo(() => [anchorRef, popupRef], []);
  const { open, items, active, actionMode } = box;
  const position = useComboboxPosition(open, anchorRef, popupRef, `${items.length}-${loading}`);
  const unavailable = unavailableText !== undefined;
  const clearable = props.clearable && !actionMode && !!value && !disabled && !unavailable;

  useOutsidePointerDown(open, refs, box.reset);

  const shown = unavailable ? unavailableText : box.typing || actionMode ? box.query : (value?.label ?? '');

  return (
    <Field id={id} label={label} hideLabel={hideLabel} helper={helper} className={className}>
      <div ref={anchorRef} className={cn(fieldControlClass, 'h-12 pr-2')}>
        <Search aria-hidden="true" className="size-[18px] shrink-0 text-ink-secondary" />
        <input
          id={id}
          type="text"
          role="combobox"
          autoComplete="off"
          aria-autocomplete="list"
          aria-expanded={open}
          aria-controls={open && items.length ? listId : undefined}
          aria-activedescendant={open && items[active] ? `${id}-opt-${active}` : undefined}
          aria-invalid={invalid || undefined}
          aria-describedby={helper ? helperIdFor(id) : undefined}
          disabled={disabled || unavailable}
          value={shown}
          placeholder={placeholder}
          onChange={box.onInputChange}
          onKeyDown={box.onKeyDown}
          spellCheck={false} onFocus={(event) => event.currentTarget.select()}
          onClick={() => !open && box.openAt()}
          onBlur={box.reset}
          className="h-full min-w-0 flex-1 bg-transparent text-body text-ink-primary outline-none placeholder:text-ink-tertiary"
        />
        {loading && open && <Spinner className="size-4 text-ink-secondary" />}
        {clearable && (
          <button
            type="button"
            tabIndex={-1}
            aria-label="Effacer la sélection"
            onMouseDown={(event) => event.preventDefault()}
            onClick={() => props.onChange?.(null)}
            className="grid size-8 shrink-0 place-items-center rounded-pill text-ink-secondary hover:bg-state-hover"
          >
            <X aria-hidden="true" className="size-4" />
          </button>
        )}
        {!actionMode && <ChevronDown aria-hidden="true" className={cn('size-4 shrink-0 text-ink-secondary transition-transform', open && 'rotate-180')} />}
      </div>
      {open && (
        <ComboboxPopup
          popupRef={popupRef}
          listId={listId}
          label={label}
          position={position}
          hasItems={items.length > 0}
          loading={loading}
          emptyText={emptyText}
        >
          {items.map((item, index) => (
            <ComboboxOptionRow
              key={item.key}
              id={`${id}-opt-${index}`}
              option={item.option}
              emptyLabel={props.emptyOptionLabel ?? ''}
              active={index === active}
              selected={!actionMode && (item.option ? item.option.value === value?.value : value === null)}
              render={renderOption}
              onPick={() => box.pick(item)}
              onHover={() => box.setActive(index)}
            />
          ))}
        </ComboboxPopup>
      )}
    </Field>
  );
}
