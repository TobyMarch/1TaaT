import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import Home from './Home'; // Import your Home component
import axios from "axios";

describe('Home component', () => {
  test('renders with required fields', () => {
    const { getByLabelText, getByText } = render(<Home />);

    // Ensure all labels are present
    expect(getByLabelText('Task Title:')).toBeInTheDocument();
    expect(getByLabelText('Task:')).toBeInTheDocument();
    expect(getByLabelText('Start Date:')).toBeInTheDocument();
    expect(getByLabelText('Due Date:')).toBeInTheDocument();
    expect(getByText('Recurring')).toBeInTheDocument();
    expect(getByLabelText('Priority:')).toBeInTheDocument();
    expect(getByText('Duration:')).toBeInTheDocument();

    // Ensure Submit button is present
    expect(getByText('Submit')).toBeInTheDocument();
  });

  test('handles input changes correctly', () => {
    const { getByLabelText } = render(<Home />);

    // Simulate input changes
    fireEvent.change(getByLabelText('Task Title:'), { target: { value: 'New Task Title' } });
    fireEvent.change(getByLabelText('Task:'), { target: { value: 'New Task Description' } });
    fireEvent.change(getByLabelText('Start Date:'), { target: { value: '2024-04-27T09:00' } });
    fireEvent.change(getByLabelText('Due Date:'), { target: { value: '2024-04-28T09:00' } });
    fireEvent.change(getByLabelText('Priority:'), { target: { value: '3' } });
    fireEvent.change(getByLabelText('Duration:'), { target: { value: 'M' } });

    // Assert that input values are updated
    expect(getByLabelText('Task Title:').value).toBe('New Task Title');
    expect(getByLabelText('Task:').value).toBe('New Task Description');
    expect(getByLabelText('Start Date:').value).toBe('2024-04-27T09:00');
    expect(getByLabelText('Due Date:').value).toBe('2024-04-28T09:00');
    expect(getByLabelText('Priority:').value).toBe('3');
    expect(getByLabelText('Duration:').value).toBe('M');
  });

  test('handles form submission correctly', () => {
    const handleSubmit = jest.fn();
    const { getByText } = render(<Home handleSubmit={handleSubmit} />);

    // Simulate form submission
    fireEvent.click(getByText('Submit'));

    // Assert that handleSubmit function is called
    expect(handleSubmit).toHaveBeenCalled();
  });
});
