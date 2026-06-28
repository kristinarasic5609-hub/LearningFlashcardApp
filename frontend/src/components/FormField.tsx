import { InputHTMLAttributes, ReactNode, TextareaHTMLAttributes } from 'react';

interface BaseFieldProps {
  label: string;
  error?: string;
  hint?: string;
}

type InputFieldProps = BaseFieldProps &
  InputHTMLAttributes<HTMLInputElement> & { as?: 'input' };

type TextareaFieldProps = BaseFieldProps &
  TextareaHTMLAttributes<HTMLTextAreaElement> & { as: 'textarea' };

type FormFieldProps = InputFieldProps | TextareaFieldProps;

export function FormField(props: FormFieldProps) {
  const { label, error, hint, as = 'input', ...rest } = props;
  const id = 'id' in rest && rest.id ? rest.id : label.toLowerCase().replace(/\s+/g, '-');

  let control: ReactNode;
  if (as === 'textarea') {
    const { as: _, ...textareaProps } = props as TextareaFieldProps;
    control = <textarea id={id} className={error ? 'input-error' : ''} {...textareaProps} />;
  } else {
    const { as: _, ...inputProps } = props as InputFieldProps;
    control = <input id={id} className={error ? 'input-error' : ''} {...inputProps} />;
  }

  return (
    <div className={`form-field ${error ? 'has-error' : ''}`}>
      <label htmlFor={id}>{label}</label>
      {control}
      {hint && !error && <span className="field-hint">{hint}</span>}
      {error && <span className="field-error" role="alert">{error}</span>}
    </div>
  );
}
